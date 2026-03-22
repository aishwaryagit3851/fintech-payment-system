package com.fintech.finance_platform_aish.controller;

import com.fintech.finance_platform_aish.dto.CardPaymentRequestDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/card")
    public TransactionResponseDTO payWithCard(@RequestBody CardPaymentRequestDTO request) {
        return paymentService.processCardPayment(request);
    }
}