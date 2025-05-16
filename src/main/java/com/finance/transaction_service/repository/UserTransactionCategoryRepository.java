package com.finance.transaction_service.repository;

import com.finance.transaction_service.entity.UserTransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTransactionCategoryRepository extends JpaRepository<UserTransactionCategory,Long> {
}
