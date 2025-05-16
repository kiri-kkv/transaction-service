package com.finance.transaction_service.dto;

import com.finance.transaction_service.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private UUID userId;
    private TransactionType type;
    private double amount;
    private String category;
    private String date;
    private String description;
    private MultipartFile attachment;
}
