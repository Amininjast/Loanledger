package com.loanledger.payment.service;

import com.loanledger.common.exception.BusinessException;
import com.loanledger.common.exception.ResourceNotFoundException;
import com.loanledger.installment.entity.Installment;
import com.loanledger.installment.enums.InstallmentStatus;
import com.loanledger.installment.repository.InstallmentRepository;
import com.loanledger.loan.entity.Loan;
import com.loanledger.loan.enums.LoanStatus;
import com.loanledger.loan.repository.LoanRepository;
import com.loanledger.payment.entity.Payment;
import com.loanledger.payment.repository.PaymentRepository;
import com.loanledger.user.entity.User;
import com.loanledger.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InstallmentRepository installmentRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              InstallmentRepository installmentRepository,
                              UserRepository userRepository,
                              LoanRepository loanRepository) {
        this.paymentRepository = paymentRepository;
        this.installmentRepository = installmentRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    /**
     * Critical path demonstrating ACID:
     * - Atomicity: payment record + installment update succeed or fail together
     * - Consistency: business rules (status, remaining amount) enforced
     * - Isolation: PESSIMISTIC_WRITE lock prevents concurrent double-payment
     * - Durability: committed by the database
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Payment payInstallment(UUID userId, UUID installmentId, BigDecimal amount, String reference) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Payment amount must be positive");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Pessimistic lock – blocks other transactions trying to pay the same installment
        Installment installment = installmentRepository.findByIdForUpdate(installmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Installment", installmentId));

        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new BusinessException("Installment is already fully paid", HttpStatus.CONFLICT);
        }
        if (installment.getStatus() == InstallmentStatus.CANCELLED) {
            throw new BusinessException("Cannot pay a cancelled installment");
        }

        Loan loan = installment.getLoan();
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException("Loan is not active; payments are not allowed");
        }

        // Ownership check (customer can only pay their own installments)
        if (!loan.getUser().getId().equals(userId)) {
            throw new BusinessException("You are not allowed to pay this installment", HttpStatus.FORBIDDEN);
        }

        BigDecimal remaining = installment.getRemainingAmount();
        if (amount.compareTo(remaining) > 0) {
            throw new BusinessException(
                    "Payment amount (%.2f) exceeds remaining balance (%.2f)"
                            .formatted(amount, remaining));
        }

        Payment payment = new Payment(user, installment, amount, reference);
        installment.applyPayment(amount);
        payment.markSuccess();

        paymentRepository.save(payment);
        // installment is managed; changes are flushed with the transaction

        // If all installments of the loan are now PAID → mark loan COMPLETED
        boolean allPaid = loan.getInstallments().stream()
                .allMatch(i -> i.getStatus() == InstallmentStatus.PAID);
        if (allPaid) {
            loan.markCompleted();
            loanRepository.save(loan);
        }

        return payment;
    }

    @Override
    public Payment getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
    }

    @Override
    public Page<Payment> getPaymentsByUser(UUID userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable);
    }
}
