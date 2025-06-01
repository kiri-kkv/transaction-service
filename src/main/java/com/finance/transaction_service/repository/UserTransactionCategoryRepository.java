package com.finance.transaction_service.repository;

import com.finance.transaction_service.entity.UserTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserTransactionCategoryRepository extends JpaRepository<UserTransactionCategory,Long> {
    UserTransactionCategory getByUserId(UUID userId);
}
