package org.balanceservice.service.impl;

import org.balanceservice.dto.BalanceResponseDTO;
import org.balanceservice.dto.DepositRequestDTO;
import org.balanceservice.dto.TransferRequestDTO;
import org.balanceservice.dto.WithdrawalRequestDTO;
import org.balanceservice.entity.AccountEntity;
import org.balanceservice.enums.TransactionType;
import org.balanceservice.exception.InsufficientFundsException;
import org.balanceservice.repository.AccountRepository;
import org.balanceservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testDeposit() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(500));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setWalletId(4L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));

        when(accountRepository.findByAccountNumberForUpdate(1L)).thenReturn(Optional.of(accountEntity));

        BalanceResponseDTO response = accountService.deposit(request);

        assertEquals(4L, response.getWalletId());
        assertEquals(BigDecimal.valueOf(1500), response.getBalance());
        verify(transactionService).createTransaction(
                eq(accountEntity),
                isNull(),
                eq(BigDecimal.valueOf(500)),
                eq(TransactionType.DEPOSIT)
        );
    }

    @Test
    void testWithdraw() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(300));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setWalletId(4L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));

        when(accountRepository.findByAccountNumberForUpdate(1L)).thenReturn(Optional.of(accountEntity));

        BalanceResponseDTO response = accountService.processWithdrawal(request);

        assertEquals(4L, response.getWalletId());
        assertEquals(BigDecimal.valueOf(700), response.getBalance());
        verify(transactionService).createTransaction(
                eq(accountEntity),
                isNull(),
                eq(BigDecimal.valueOf(300)),
                eq(TransactionType.WITHDRAWAL)
        );
    }

    @Test
    void testWithdrawInsufficientFunds() {
        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setWalletId(1L);
        request.setAmount(BigDecimal.valueOf(1500));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setWalletId(1L);
        accountEntity.setBalance(BigDecimal.valueOf(1000));

        when(accountRepository.findByAccountNumberForUpdate(1L)).thenReturn(Optional.of(accountEntity));

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            accountService.processWithdrawal(request);
        });

        assertEquals("Insufficient funds on source account: 1", exception.getMessage());
        verify(accountRepository, never()).save(any(AccountEntity.class));
        verify(transactionService, never()).createTransaction(any(), any(), any(), any());
    }

    @Test
    void testTransfer() {
        TransferRequestDTO request = new TransferRequestDTO();
        request.setSourceWalletId(1L);
        request.setTargetWalletId(2L);
        request.setAmount(BigDecimal.valueOf(500));

        AccountEntity sourceAccountEntity = new AccountEntity();
        sourceAccountEntity.setId(1L);
        sourceAccountEntity.setWalletId(4L);
        sourceAccountEntity.setBalance(BigDecimal.valueOf(1000));

        AccountEntity targetAccountEntity = new AccountEntity();
        targetAccountEntity.setId(2L);
        targetAccountEntity.setWalletId(4L);
        targetAccountEntity.setBalance(BigDecimal.valueOf(2000));

        when(accountRepository.findByAccountNumberForUpdate(1L)).thenReturn(Optional.of(sourceAccountEntity));
        when(accountRepository.findByAccountNumberForUpdate(2L)).thenReturn(Optional.of(targetAccountEntity));

        BalanceResponseDTO response = accountService.processTransfer(request);

        assertEquals(4L, response.getWalletId());
        assertEquals(BigDecimal.valueOf(500), response.getBalance());
        assertEquals(BigDecimal.valueOf(2500), targetAccountEntity.getBalance());
        verify(transactionService).createTransaction(
                eq(sourceAccountEntity),
                eq(targetAccountEntity),
                eq(BigDecimal.valueOf(500)),
                eq(TransactionType.TRANSFER)
        );
    }

}