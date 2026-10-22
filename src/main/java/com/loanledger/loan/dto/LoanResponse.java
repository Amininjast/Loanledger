package com.loanledger.loan.dto;

import com.loanledger.installment.dto.InstallmentResponse;
import com.loanledger.loan.entity.Loan;
import com.loanledger.loan.enums.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record LoanResponse(
        UUID id,
        UUID userId,
        BigDecimal principalAmount,
        BigDecimal annualInterestRate,
        int termInMonths,
        LocalDate startDate,
        LoanStatus status,
        List<InstallmentResponse> installments
)
