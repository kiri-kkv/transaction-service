package com.finance.transaction_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserIdDto {
    @NotNull(message = "User ID cannot be null")
    UUID userId;
}
