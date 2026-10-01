package com.loanledger.payment.entity;

import com.loanledger.common.util.BaseEntity;
import com.loanledger.installment.entity.Installment;
import com.loanledger.user.entity.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_user_id", columnList = "user_id"),
        @Index(name = "idx_payments_installment_id", columnList = "installment_id"),
        @Index(name = "idx_payments_status", columnList = "status")
})
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "installment_id", nullable = false)
    private Installment installment;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(nullable = false)
    private Instant paidAt;

    @Column(length = 100)
    private String reference;

    protected Payment() {}

    public Payment(User user, Installment installment, BigDecimal amount, String reference) {
        this.user = Objects.requireNonNull(user);
        this.installment = Objects.requireNonNull(installment);
        this.amount = requirePositive(amount, "amount");
        this.reference = reference;
        this.status = PaymentStatus.PENDING;
        this.paidAt = Instant.now();
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public Installment getInstallment() { return installment; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public Instant getPaidAt() { return paidAt; }
    public String getReference() { return reference; }

    public void markSuccess() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Only PENDING payments can be marked SUCCESS");
        }
        this.status = PaymentStatus.SUCCESS;
    }

    public void markFailed() { this.status = PaymentStatus.FAILED; }

    public void reverse() {
        if (this.status != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Only SUCCESS payments can be reversed");
        }
        this.status = PaymentStatus.REVERSED;
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
        if (!(o instanceof Payment payment)) return false;
        return id != null && Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
