package org.balanceservice.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Используется для списания суммы со счета
 */
@Data
public class WithdrawalRequestDTO {

    @NotNull(message = "Wallet ID cannot be null")
    private Long walletId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

}
