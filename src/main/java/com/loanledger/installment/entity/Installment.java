package com.loanledger.installment.entity;

import com.loanledger.common.util.BaseEntity;
import com.loanledger.installment.enums.InstallmentStatus;
import com.loanledger.loan.entity.Loan;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "installments", indexes = {
        @Index(name = "idx_installments_loan_id", columnList = "loan_id"),
        @Index(name = "idx_installments_due_date", columnList = "due_date"),
        @Index(name = "idx_installments_status", columnList = "status")
})
public class Installment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private int installmentNumber;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InstallmentStatus status;

    protected Installment() {}

    public Installment(Loan loan, int installmentNumber, BigDecimal amount, LocalDate dueDate) {
        this.loan = Objects.requireNonNull(loan);
        if (installmentNumber <= 0) throw new IllegalArgumentException("installmentNumber must be positive");
        this.installmentNumber = installmentNumber;
        this.amount = requirePositive(amount, "amount");
        this.dueDate = Objects.requireNonNull(dueDate);
        this.status = InstallmentStatus.PENDING;
        this.paidAmount = BigDecimal.ZERO;
    }

    public UUID getId() { return id; }
    public Loan getLoan() { return loan; }
    void setLoan(Loan loan) { this.loan = loan; }
    public int getInstallmentNumber() { return installmentNumber; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public BigDecimal getRemainingAmount() { return amount.subtract(paidAmount); }
    public LocalDate getDueDate() { return dueDate; }
    public InstallmentStatus getStatus() { return status; }

    public BigDecimal applyPayment(BigDecimal paymentAmount) {
        if (status == InstallmentStatus.PAID || status == InstallmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a " + status + " installment");
        }
        BigDecimal remaining = getRemainingAmount();
        BigDecimal applied = paymentAmount.min(remaining);
        this.paidAmount = this.paidAmount.add(applied);
        if (this.paidAmount.compareTo(this.amount) >= 0) {
            this.status = InstallmentStatus.PAID;
        } else if (this.paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = InstallmentStatus.PARTIAL;
        }
        return applied;
    }

    public void markOverdue() {
        if (status == InstallmentStatus.PENDING || status == InstallmentStatus.PARTIAL) {
            this.status = InstallmentStatus.OVERDUE;
        }
    }

    public void cancel() {
        this.status = InstallmentStatus.CANCELLED;
    }

    private static BigDecimal requirePositive(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Installment that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
