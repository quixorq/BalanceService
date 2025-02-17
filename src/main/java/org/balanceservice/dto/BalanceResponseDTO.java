package org.balanceservice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Используется для предоставления текущего баланса по счету
 */
@Data
public class BalanceResponseDTO {

    private Long walletId;

    private BigDecimal balance;

}
