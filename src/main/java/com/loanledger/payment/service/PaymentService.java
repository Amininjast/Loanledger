package com.loanledger.payment.service;

import com.loanledger.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    /**
     * Pays an installment.
     * Uses pessimistic locking + @Transactional to guarantee ACID and prevent concurrent over-payment.
     */
    Payment payInstallment(UUID userId, UUID installmentId, BigDecimal amount, String reference);

    Payment getPayment(UUID paymentId);

    Page<Payment> getPaymentsByUser(UUID userId, Pageable pageable);
}
