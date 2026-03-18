package com.fintech.finance_platform_aish.controller;



import com.fintech.finance_platform_aish.dto.AccountCreateRequestDTO;
import com.fintech.finance_platform_aish.dto.AccountResponseDTO;
import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponseDTO createAccount(@RequestBody AccountCreateRequestDTO request) {
        System.out.println("entered accountcontroller create control methed");
        return accountService.createAccount(request);
    }

    @GetMapping("/user/{userId}")
    public List<AccountResponseDTO> getUserAccounts(@PathVariable Long userId) {
        return accountService.getUserAccounts(userId);
    }
}
