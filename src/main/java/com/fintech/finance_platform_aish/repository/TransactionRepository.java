package com.fintech.finance_platform_aish.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fintech.finance_platform_aish.entity.Transaction;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.fromAccount.id = :accountId
       OR t.toAccount.id = :accountId
    """)
    Page<Transaction> findTransactionsForAccount(Long accountId, Pageable pageable);

    @Query("""
    SELECT COUNT(t) FROM Transaction t 
    WHERE t.fromAccount.id = :accountId
    AND t.timestamp >= :time
    """)
    long countRecentTransactions(Long accountId, LocalDateTime time);
}
