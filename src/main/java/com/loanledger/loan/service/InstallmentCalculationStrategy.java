package com.loanledger.loan.service;

import com.loanledger.installment.entity.Installment;
import com.loanledger.loan.entity.Loan;

import java.util.List;

/**
 * Strategy for generating installments of a loan.
 * Open/Closed Principle: new calculation methods can be added without changing LoanService.
 */
public interface InstallmentCalculationStrategy {

    /**
     * Generates the list of installments for the given loan.
     * The returned installments are not yet persisted; the caller (LoanService) attaches them to the loan.
     */
    List<Installment> generate(Loan loan);
}
