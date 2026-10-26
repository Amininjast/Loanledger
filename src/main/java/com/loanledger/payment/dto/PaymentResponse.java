package com.loanledger.payment.dto;

import com.loanledger.payment.entity.Payment;
import com.loanledger.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID installmentId,
        BigDecimal amount,
        PaymentStatus status,
        Instant paidAt,
        String reference
) {
    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getInstallment().getId(),
                p.getAmount(),
                p.getStatus(),
                p.getPaidAt(),
                p.getReference()
        );
    }
}
