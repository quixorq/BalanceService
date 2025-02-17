package org.balanceservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Используется для зачисления суммы на счет
 */
@Data
public class DepositRequestDTO {

    @NotNull(message = "walletId cannot be null")
    private Long walletId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

}
