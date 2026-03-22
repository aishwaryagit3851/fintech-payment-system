package com.fintech.finance_platform_aish.controller;

import com.fintech.finance_platform_aish.dto.CardResponseDTO;
import com.fintech.finance_platform_aish.dto.CreateCardRequestDTO;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.UserRepository;
import com.fintech.finance_platform_aish.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.OptionalInt;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    public CardController(CardService cardService,UserRepository userRepository) {
        this.cardService = cardService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public CardResponseDTO createCard(@RequestBody CreateCardRequestDTO request,
                                      Authentication authentication) {
        System.out.print("email");
        String email = authentication.getName();
        System.out.print("email");
        System.out.println(email);
        Optional<User> user = userRepository.findByEmail(email);
        // fetch userId from email (you already have this logic)
        Long userId = user.get().getId(); // replace with actual fetch

        return cardService.createCard(request, userId);
    }
}
