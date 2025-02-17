package org.balanceservice.service.impl;

import org.balanceservice.entity.AccountEntity;
import org.balanceservice.enums.TransactionType;
import org.balanceservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testCreateTransaction() {
        AccountEntity sourceAccount = new AccountEntity();
        sourceAccount.setId(1L);

        AccountEntity targetAccount = new AccountEntity();
        targetAccount.setId(2L);

        BigDecimal amount = BigDecimal.valueOf(500);
        TransactionType type = TransactionType.TRANSFER;

        transactionService.createTransaction(sourceAccount, targetAccount, amount, type);

        verify(transactionRepository).save(argThat(transaction -> transaction.getSourceAccountEntity().equals(sourceAccount) &&
                transaction.getTargetAccountEntity().equals(targetAccount) &&
                transaction.getAmount().equals(amount) &&
                transaction.getType() == type &&
                transaction.getTimestamp() != null));
    }

}
