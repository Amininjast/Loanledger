package com.loanledger.installment.repository;

import com.loanledger.installment.entity.Installment;
import com.loanledger.installment.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InstallmentRepository extends JpaRepository<Installment, UUID> {
    List<Installment> findByLoanIdOrderByInstallmentNumberAsc(UUID loanId);
    List<Installment> findByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Installment i WHERE i.id = :id")
    Optional<Installment> findByIdForUpdate(@Param("id") UUID id);
}
