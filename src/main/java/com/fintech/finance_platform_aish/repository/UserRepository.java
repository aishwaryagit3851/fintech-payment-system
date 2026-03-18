package com.fintech.finance_platform_aish.repository;

import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


}