package com.loanledger.payment.controller;

import com.loanledger.common.response.ApiResponse;
import com.loanledger.payment.dto.PayInstallmentRequest;
import com.loanledger.payment.dto.PaymentResponse;
import com.loanledger.payment.entity.Payment;
import com.loanledger.payment.service.PaymentService;
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
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


}
