package com.loanledger.loan.service;

import com.loanledger.installment.entity.Installment;
import com.loanledger.loan.entity.Loan;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classic equal (fixed) installment calculation.
 * Each installment has the same total amount (principal + interest share simplified).
 * For portfolio clarity we use a straightforward equal-principal-plus-interest split.
 */
@Component
public class EqualInstallmentStrategy implements InstallmentCalculationStrategy {

    private static final int MONEY_SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    @Override
    public List<Installment> generate(Loan loan) {
        int months = loan.getTermInMonths();
        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal annualRate = loan.getAnnualInterestRate();

        // Monthly interest rate (e.g. 18% -> 0.18 / 12)
        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), 10, ROUNDING)
                .divide(BigDecimal.valueOf(12), 10, ROUNDING);

        BigDecimal installmentAmount = calculateFixedInstallment(principal, monthlyRate, months);
        LocalDate dueDate = loan.getStartDate().plusMonths(1);

        List<Installment> installments = new ArrayList<>(months);
        BigDecimal remainingPrincipal = principal;

        for (int i = 1; i <= months; i++) {
            BigDecimal interest = remainingPrincipal.multiply(monthlyRate).setScale(MONEY_SCALE, ROUNDING);
            BigDecimal principalPart = installmentAmount.subtract(interest);

            // Last installment adjustment to avoid rounding remainder
            if (i == months) {
                principalPart = remainingPrincipal;
                installmentAmount = principalPart.add(interest).setScale(MONEY_SCALE, ROUNDING);
            }

            Installment installment = new Installment(loan, i, installmentAmount, dueDate);
            installments.add(installment);

            remainingPrincipal = remainingPrincipal.subtract(principalPart);
            dueDate = dueDate.plusMonths(1);
        }

        return installments;
    }

    /**
     * Standard amortization formula:
     * A = P * r * (1+r)^n / ((1+r)^n - 1)
     * When rate is zero, simply divide principal by months.
     */
    private BigDecimal calculateFixedInstallment(BigDecimal principal, BigDecimal monthlyRate, int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), MONEY_SCALE, ROUNDING);
        }

        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal power = onePlusRate.pow(months);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(power);
        BigDecimal denominator = power.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, MONEY_SCALE, ROUNDING);
    }
}
