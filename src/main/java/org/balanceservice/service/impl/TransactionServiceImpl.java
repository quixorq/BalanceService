package org.balanceservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.balanceservice.dto.TransactionDTO;
import org.balanceservice.dto.TransactionHistoryRequestDTO;
import org.balanceservice.dto.TransactionHistoryResponseDTO;
import org.balanceservice.entity.AccountEntity;
import org.balanceservice.entity.TransactionEntity;
import org.balanceservice.enums.TransactionType;
import org.balanceservice.mapper.TransactionMapper;
import org.balanceservice.repository.TransactionRepository;
import org.balanceservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public void createTransaction(AccountEntity sourceAccount,
                                  AccountEntity targetAccount,
                                  BigDecimal amount,
                                  TransactionType type) {
        TransactionEntity transaction = new TransactionEntity();
        transaction.setSourceAccountEntity(sourceAccount);
        transaction.setTargetAccountEntity(targetAccount);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    /**
     * Получение истории транзакций за указанный период.
     */
    @Override
    public TransactionHistoryResponseDTO getTransactionHistory(TransactionHistoryRequestDTO request) {
        List<TransactionEntity> transactions = transactionRepository.findByAccountIdAndTimestampBetween(
                request.getWalletId(),
                request.getStartDate(),
                request.getEndDate()
        );

        List<TransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);

        TransactionHistoryResponseDTO response = new TransactionHistoryResponseDTO();
        response.setTransactions(transactionDTOs);

        return response;
    }
}
