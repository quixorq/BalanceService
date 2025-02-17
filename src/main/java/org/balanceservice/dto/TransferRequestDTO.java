package org.balanceservice.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Используется для перевода суммы с одного счета на другой
 */
@Data
public class TransferRequestDTO {

    @NotNull(message = "Source walletId cannot be null")
    private Long sourceWalletId;

    @NotNull(message = "Target walletId cannot be null")
    private Long targetWalletId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

}
