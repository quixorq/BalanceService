package org.balanceservice.service;

import org.balanceservice.dto.TransactionHistoryRequestDTO;
import org.balanceservice.dto.TransactionHistoryResponseDTO;
import org.balanceservice.entity.AccountEntity;
import org.balanceservice.enums.TransactionType;

import java.math.BigDecimal;

public interface TransactionService {

    void createTransaction(AccountEntity sourceAccount,
                           AccountEntity targetAccount,
                           BigDecimal amount,
                           TransactionType type);

    TransactionHistoryResponseDTO getTransactionHistory(TransactionHistoryRequestDTO request);
}
