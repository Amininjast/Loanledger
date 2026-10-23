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

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> pay(
            @Valid @RequestBody PayInstallmentRequest request,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Payment payment = paymentService.payInstallment(
                userId,
                request.installmentId(),
                request.amount(),
                request.reference()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Payment successful", PaymentResponse.from(payment)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(@PathVariable UUID id) {
        Payment payment = paymentService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.ok(PaymentResponse.from(payment)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> myPayments(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        UUID userId = (UUID) authentication.getPrincipal();
        Page<PaymentResponse> page = paymentService.getPaymentsByUser(userId, pageable)
                .map(PaymentResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }
}
