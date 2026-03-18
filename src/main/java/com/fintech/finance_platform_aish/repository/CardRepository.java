package com.fintech.finance_platform_aish.repository;

import com.fintech.finance_platform_aish.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findByUser_Id(Long userId);
}
