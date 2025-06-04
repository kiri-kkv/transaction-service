package com.finance.transaction_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCategoryDto {
    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull
    @NotBlank(message = "Category is required")
    private String category;
}
