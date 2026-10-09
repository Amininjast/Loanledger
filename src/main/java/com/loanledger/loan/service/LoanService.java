package com.loanledger.loan.service;

import com.loanledger.loan.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface LoanService {

    /**
     * Creates a DRAFT loan, generates installments via strategy, and returns the persisted loan.
     */
    Loan createLoan(UUID userId, BigDecimal principalAmount, BigDecimal annualInterestRate,
                    int termInMonths, LocalDate startDate);

    /**
     * Activates a DRAFT loan (installments must already exist).
     */
    Loan activateLoan(UUID loanId);

    /**
     * Cancels a loan that is not yet COMPLETED or DEFAULTED.
     */
    Loan cancelLoan(UUID loanId);

    Loan getLoan(UUID loanId);

    Loan getLoanWithInstallments(UUID loanId);

    Page<Loan> getLoansByUser(UUID userId, Pageable pageable);
}
