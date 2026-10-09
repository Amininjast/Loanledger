package com.loanledger.loan.service;

import com.loanledger.common.exception.BusinessException;
import com.loanledger.common.exception.ResourceNotFoundException;
import com.loanledger.installment.entity.Installment;
import com.loanledger.loan.entity.Loan;
import com.loanledger.loan.repository.LoanRepository;
import com.loanledger.user.entity.User;
import com.loanledger.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final InstallmentCalculationStrategy calculationStrategy;

    public LoanServiceImpl(LoanRepository loanRepository,
                           UserRepository userRepository,
                           InstallmentCalculationStrategy calculationStrategy) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.calculationStrategy = calculationStrategy;
    }

    @Override
    @Transactional
    public Loan createLoan(UUID userId, BigDecimal principalAmount, BigDecimal annualInterestRate,
                           int termInMonths, LocalDate startDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.isEnabled()) {
            throw new BusinessException("User account is disabled", HttpStatus.FORBIDDEN);
        }

        Loan loan = new Loan(user, principalAmount, annualInterestRate, termInMonths, startDate);

        List<Installment> installments = calculationStrategy.generate(loan);
        installments.forEach(loan::addInstallment);

        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan activateLoan(UUID loanId) {
        Loan loan = getLoanWithInstallments(loanId);
        try {
            loan.activate();
        } catch (IllegalStateException ex) {
            throw new BusinessException(ex.getMessage());
        }
        return loanRepository.save(loan);
    }

    @Override
    @Transactional
    public Loan cancelLoan(UUID loanId) {
        Loan loan = getLoanWithInstallments(loanId);
        try {
            loan.cancel();
        } catch (IllegalStateException ex) {
            throw new BusinessException(ex.getMessage());
        }
        return loanRepository.save(loan);
    }

    @Override
    public Loan getLoan(UUID loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    @Override
    public Loan getLoanWithInstallments(UUID loanId) {
        return loanRepository.findByIdWithInstallments(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    @Override
    public Page<Loan> getLoansByUser(UUID userId, Pageable pageable) {
        return loanRepository.findByUserId(userId, pageable);
    }
}
