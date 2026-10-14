package com.loanledger.loan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateLoanRequest(
        @NotNull @DecimalMin(value = "0.01", message = "Principal must be positive")
        BigDecimal principalAmount,

        @NotNull @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
        BigDecimal annualInterestRate,

        @Min(value = 1, message = "Term must be at least 1 month")
        int termInMonths,

        @NotNull
        LocalDate startDate
) {}
