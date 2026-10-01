package com.loanledger.payment.repository;

import com.loanledger.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Page<Payment> findByUserId(UUID userId, Pageable pageable);
    List<Payment> findByInstallmentId(UUID installmentId);
}
