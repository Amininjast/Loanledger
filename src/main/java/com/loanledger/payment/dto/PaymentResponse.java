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
)
