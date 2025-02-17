package org.balanceservice.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.balanceservice.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Используется для представления отдельной транзакции
 */
@Data
public class TransactionDTO {

    private Long id;

    @NotNull(message = "Type cannot be null")
    private TransactionType type;

    @NotNull(message = "Amount cannot be null")
    @PositiveOrZero(message = "Amount must be non-negative")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    private Long sourceAccountId;

    private Long targetAccountId;

    @NotNull(message = "Timestamp cannot be null")
    private LocalDateTime timestamp;

}
