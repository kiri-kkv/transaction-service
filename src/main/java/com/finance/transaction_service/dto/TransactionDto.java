package com.finance.transaction_service.dto;

import com.finance.transaction_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private UUID userId;
    private TransactionType type;
    private BigDecimal amount;
    private String category;
    private Date date;
    private String description;
}
