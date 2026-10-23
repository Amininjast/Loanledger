package com.loanledger.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PayInstallmentRequest(
        @NotNull
        UUID installmentId,

        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,

        String reference
) {}
