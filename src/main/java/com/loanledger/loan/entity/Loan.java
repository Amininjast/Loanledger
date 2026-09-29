package com.loanledger.loan.entity;

import com.loanledger.common.util.BaseEntity;
import com.loanledger.installment.entity.Installment;
import com.loanledger.loan.enums.LoanStatus;
import com.loanledger.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "loans", indexes = {
        @Index(name = "idx_loans_user_id", columnList = "user_id"),
        @Index(name = "idx_loans_status", columnList = "status")
})
public class Loan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualInterestRate;

    @Column(nullable = false)
    private int termInMonths;

    @Column(nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("installmentNumber ASC")
    private List<Installment> installments = new ArrayList<>();

    protected Loan() {}

    public Loan(User user, BigDecimal principalAmount, BigDecimal annualInterestRate,
                int termInMonths, LocalDate startDate) {
        this.user = Objects.requireNonNull(user);
        this.principalAmount = requirePositive(principalAmount, "principalAmount");
        this.annualInterestRate = requireNonNegative(annualInterestRate, "annualInterestRate");
        if (termInMonths <= 0) throw new IllegalArgumentException("termInMonths must be positive");
        this.termInMonths = termInMonths;
        this.startDate = Objects.requireNonNull(startDate);
        this.status = LoanStatus.DRAFT;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public BigDecimal getPrincipalAmount() { return principalAmount; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public int getTermInMonths() { return termInMonths; }
    public LocalDate getStartDate() { return startDate; }
    public LoanStatus getStatus() { return status; }
    public List<Installment> getInstallments() { return Collections.unmodifiableList(installments); }

    public void addInstallment(Installment installment) {
        installments.add(installment);
        installment.setLoan(this);
    }

    public void activate() {
        if (this.status != LoanStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT loans can be activated");
        }
        if (installments.isEmpty()) {
            throw new IllegalStateException("Cannot activate loan without installments");
        }
        this.status = LoanStatus.ACTIVE;
    }

    public void markCompleted() { this.status = LoanStatus.COMPLETED; }
    public void markDefaulted() { this.status = LoanStatus.DEFAULTED; }

    public void cancel() {
        if (this.status == LoanStatus.COMPLETED || this.status == LoanStatus.DEFAULTED) {
            throw new IllegalStateException("Cannot cancel a completed or defaulted loan");
        }
        this.status = LoanStatus.CANCELLED;
        installments.forEach(Installment::cancel);
    }

    private static BigDecimal requirePositive(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String field) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(field + " must be non-negative");
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan loan)) return false;
        return id != null && Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
