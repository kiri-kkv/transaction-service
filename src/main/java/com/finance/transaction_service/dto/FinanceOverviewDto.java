package com.finance.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceOverviewDto {
    private UUID userId;
    private String fromDate;
    private String toDate;
}
