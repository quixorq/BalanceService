package org.balanceservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Используется для запроса выписки по транзакциям за определённый период
 */
@Data
public class TransactionHistoryRequestDTO {

    @NotNull(message = "Wallet ID cannot be null")
    private Long walletId;

    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;

}
