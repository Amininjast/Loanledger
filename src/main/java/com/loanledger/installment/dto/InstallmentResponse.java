package com.loanledger.installment.dto;

import com.loanledger.installment.entity.Installment;
import com.loanledger.installment.enums.InstallmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InstallmentResponse(
        UUID id,
        int installmentNumber,
        BigDecimal amount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        LocalDate dueDate,
        InstallmentStatus status
) {
    public static InstallmentResponse from(Installment i) {
        return new InstallmentResponse(
                i.getId(),
                i.getInstallmentNumber(),
                i.getAmount(),
                i.getPaidAmount(),
                i.getRemainingAmount(),
                i.getDueDate(),
                i.getStatus()
        );
    }
}
