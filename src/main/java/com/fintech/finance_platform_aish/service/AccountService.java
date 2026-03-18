package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.dto.AccountCreateRequestDTO;
import com.fintech.finance_platform_aish.dto.AccountResponseDTO;
import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.AccountRepository;
import com.fintech.finance_platform_aish.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public AccountResponseDTO createAccount(AccountCreateRequestDTO request) {
        System.out.println("entered Account service create account method");
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(request.getInitialDeposit());
        account.setAccountType(request.getAccountType());
        account.setUser(user);

        Account saved = accountRepository.save(account);

        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(saved.getId());
        dto.setAccountNumber(saved.getAccountNumber());
        dto.setBalance(saved.getBalance());
        dto.setAccountType(saved.getAccountType());

            return dto;
    }

    public List<AccountResponseDTO> getUserAccounts(Long userId) {

        List<AccountResponseDTO> accounts = accountRepository.findByUserId(userId);

        return accounts.stream().map(account -> {

            AccountResponseDTO dto = new AccountResponseDTO();

            dto.setId(account.getId());
            dto.setAccountNumber(account.getAccountNumber());
            dto.setBalance(account.getBalance());
            dto.setAccountType(account.getAccountType());

            return dto;

        }).toList();
    }

    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
}
