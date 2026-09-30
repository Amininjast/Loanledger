package com.loanledger.loan.repository;

import com.loanledger.loan.entity.Loan;
import com.loanledger.loan.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Page<Loan> findByUserId(UUID userId, Pageable pageable);
    List<Loan> findByUserIdAndStatus(UUID userId, LoanStatus status);

    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.installments WHERE l.id = :id")
    Optional<Loan> findByIdWithInstallments(@Param("id") UUID id);
}
