package org.balanceservice.dto;

import lombok.Data;

import java.util.List;

/**
 * Используется для возврата выписки по операциям за указанный период
 */
@Data
public class TransactionHistoryResponseDTO {

    private List<TransactionDTO> transactions;

}
