package org.balanceservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class AccountDTO {

    private Long id;

    private Long walletId;

    @NotNull(message = "Balance cannot be null")
    @PositiveOrZero(message = "Balance must be non-negative")
    private BigDecimal balance;

}