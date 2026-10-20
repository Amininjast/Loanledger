package com.loanledger.loan.controller;

import com.loanledger.common.response.ApiResponse;
import com.loanledger.loan.dto.CreateLoanRequest;
import com.loanledger.loan.dto.LoanResponse;
import com.loanledger.loan.entity.Loan;
import com.loanledger.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> create(
            @Valid @RequestBody CreateLoanRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Loan loan = loanService.createLoan(
                userId,
                request.principalAmount(),
                request.annualInterestRate(),
                request.termInMonths(),
                request.startDate()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Loan created", LoanResponse.from(loan)));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<LoanResponse>> activate(@PathVariable UUID id) {
        Loan loan = loanService.activateLoan(id);
        return ResponseEntity.ok(ApiResponse.ok("Loan activated", LoanResponse.from(loan)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LoanResponse>> cancel(@PathVariable UUID id) {
        Loan loan = loanService.cancelLoan(id);
        return ResponseEntity.ok(ApiResponse.ok("Loan cancelled", LoanResponse.from(loan)));
    }


}
