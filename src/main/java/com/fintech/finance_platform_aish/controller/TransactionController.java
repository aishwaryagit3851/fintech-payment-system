package com.fintech.finance_platform_aish.controller;



import com.fintech.finance_platform_aish.dto.DepositWithdrawRequestDTO;
import com.fintech.finance_platform_aish.dto.TransactionRequestDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.entity.Transaction;
import com.fintech.finance_platform_aish.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public TransactionResponseDTO transferMoney(@Valid @RequestBody TransactionRequestDTO request) {

        return transactionService.transferMoney(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
    }

    @GetMapping("/account/{accountId}")
    public Page<TransactionResponseDTO> getTransactions(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return transactionService.getAccountTransactions(accountId, page, size);
    }

    @PostMapping("/deposit")
    public TransactionResponseDTO deposit(@RequestBody DepositWithdrawRequestDTO request) {

        return transactionService.deposit(
                request.getAccountId(),
                request.getAmount()
        );
    }

    @PostMapping("/withdraw")
    public TransactionResponseDTO withdraw(@RequestBody DepositWithdrawRequestDTO request) {

        return transactionService.withdraw(
                request.getAccountId(),
                request.getAmount()
        );
    }


}
