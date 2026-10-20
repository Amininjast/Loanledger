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


}
