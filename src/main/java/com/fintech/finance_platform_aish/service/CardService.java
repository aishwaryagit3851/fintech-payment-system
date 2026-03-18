package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.dto.CardResponseDTO;
import com.fintech.finance_platform_aish.dto.CreateCardRequestDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.entity.Card;
import com.fintech.finance_platform_aish.entity.Transaction;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.AccountRepository;
import com.fintech.finance_platform_aish.repository.CardRepository;
import com.fintech.finance_platform_aish.repository.TransactionRepository;
import com.fintech.finance_platform_aish.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public CardService(CardRepository cardRepository,
                       AccountRepository accountRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public CardResponseDTO createCard(CreateCardRequestDTO request, Long userId) {

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Card card = new Card();

        card.setCardNumber(generateCardNumber());
        card.setCardType(request.getCardType());
        card.setBankName(request.getBankName());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setAccount(account);
        Optional<User> user = userRepository.findById(userId);
        card.setUser(user.get());

        Card saved = cardRepository.save(card);

        CardResponseDTO response = new CardResponseDTO();
        response.setCardNumber(saved.getCardNumber());
        response.setCardType(saved.getCardType());
        response.setBankName(saved.getBankName());
        response.setExpiryDate(saved.getExpiryDate());

        return response;
    }
    private String generateCardNumber() {
        return "4111" + System.currentTimeMillis(); // simple mock
    }



}