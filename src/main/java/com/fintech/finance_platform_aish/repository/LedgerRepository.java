package com.fintech.finance_platform_aish.repository;

import com.fintech.finance_platform_aish.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<LedgerEntry , Long> {
}
