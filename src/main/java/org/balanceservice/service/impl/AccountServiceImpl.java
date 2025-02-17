package org.balanceservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balanceservice.utils.NumberGenerator;
import org.balanceservice.dto.AccountDTO;
import org.balanceservice.dto.BalanceResponseDTO;
import org.balanceservice.dto.DepositRequestDTO;
import org.balanceservice.dto.TransferRequestDTO;
import org.balanceservice.dto.WithdrawalRequestDTO;
import org.balanceservice.entity.AccountEntity;

import org.balanceservice.enums.TransactionType;
import org.balanceservice.exception.AccountNotFoundException;
import org.balanceservice.exception.DuplicateNumberException;
import org.balanceservice.exception.InsufficientFundsException;
import org.balanceservice.exception.InvalidAmountException;
import org.balanceservice.mapper.AccountMapper;
import org.balanceservice.repository.AccountRepository;
import org.balanceservice.service.AccountService;
import org.balanceservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final TransactionService transactionService;

    /**
     * Создание нового счёта
     */
    @Override
    @Transactional
    public AccountDTO createAccount() {
        Long walletId = NumberGenerator.generateWalletId();
        log.info("Generated wallet ID: {}", walletId);

        if (accountRepository.existsByWalletId(walletId)) {
            log.warn("Duplicate wallet ID found: {}", walletId);
            throw new DuplicateNumberException("Wallet ID already exists: " + walletId);
        }

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setWalletId(walletId);
        accountEntity.setBalance(BigDecimal.ZERO);

        AccountEntity savedAccountEntity = accountRepository.save(accountEntity);
        log.info("Account successfully created with wallet ID: {}", savedAccountEntity.getWalletId());

        return accountMapper.toDTO(savedAccountEntity);
    }

    /**
     * Зачисление средств на счет.
     */
    @Override
    @Transactional
    public BalanceResponseDTO deposit(DepositRequestDTO request) {
        validateAmount(request.getAmount());

        AccountEntity accountEntity = findAccountByAccountNumberForUpdate(request.getWalletId());
        if (Objects.isNull(accountEntity)) {
            throw new AccountNotFoundException("Account not found with number: " + request.getWalletId());
        }
        BigDecimal newBalance = accountEntity.getBalance().add(request.getAmount());
        accountEntity.setBalance(newBalance);

        accountRepository.save(accountEntity);
        log.info("Deposited amount {} to account {}", request.getAmount(), accountEntity.getId());

        transactionService.createTransaction(accountEntity, null, request.getAmount(), TransactionType.DEPOSIT);

        return createBalanceResponse(accountEntity);
    }

    /**
     * Списание средств со счета.
     */
    @Override
    @Transactional
    public BalanceResponseDTO processWithdrawal(WithdrawalRequestDTO request) {
        validateAmount(request.getAmount());

        AccountEntity accountEntity = findAccountByAccountNumberForUpdate(request.getWalletId());
        if (accountEntity.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds on source account: " + accountEntity.getWalletId());
        }

        BigDecimal newBalance = accountEntity.getBalance().subtract(request.getAmount());
        accountEntity.setBalance(newBalance);

        accountRepository.save(accountEntity);
        log.info("Withdrew amount {} from account {}", request.getAmount(), accountEntity.getId());

        transactionService.createTransaction(accountEntity, null, request.getAmount(), TransactionType.WITHDRAWAL);

        return createBalanceResponse(accountEntity);
    }

    /**
     * Перевод средств между счетами.
     */
    @Override
    @Transactional
    public BalanceResponseDTO processTransfer(TransferRequestDTO request) {
        validateAmount(request.getAmount());

        AccountEntity sourceAccountEntity = findAccountByAccountNumberForUpdate(request.getSourceWalletId());
        AccountEntity targetAccountEntity = findAccountByAccountNumberForUpdate(request.getTargetWalletId());

        if (sourceAccountEntity.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds on source account: " + sourceAccountEntity.getWalletId());
        }

        BigDecimal newSourceBalance = sourceAccountEntity.getBalance().subtract(request.getAmount());
        BigDecimal newTargetBalance = targetAccountEntity.getBalance().add(request.getAmount());
        sourceAccountEntity.setBalance(newSourceBalance);
        targetAccountEntity.setBalance(newTargetBalance);

        log.info("Transferred amount {} from account {} (new balance: {}) to account {} (new balance: {})",
                request.getAmount(),
                sourceAccountEntity.getWalletId(), newSourceBalance,
                targetAccountEntity.getWalletId(), newTargetBalance);

        transactionService.createTransaction(sourceAccountEntity, targetAccountEntity, request.getAmount(), TransactionType.TRANSFER);

        return createBalanceResponse(sourceAccountEntity);
    }

    /**
     * Получение баланса счета.
     */
    @Override
    public BalanceResponseDTO getBalance(Long walletId) {
        AccountEntity accountEntity = accountRepository.findByWalletId(walletId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with walletId: " + walletId));

        return accountMapper.toBalanceResponseDTO(accountEntity);
    }

    /**
     * Валидация суммы.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive and non-null");
        }
    }

    /**
     * Поиск счета с пессимистичной блокировкой.
     */
    private AccountEntity findAccountByAccountNumberForUpdate(Long walletId) {
        return accountRepository.findByAccountNumberForUpdate(walletId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + walletId));
    }

    private BalanceResponseDTO createBalanceResponse(AccountEntity accountEntity) {
        BalanceResponseDTO response = new BalanceResponseDTO();
        response.setBalance(accountEntity.getBalance());
        response.setWalletId(accountEntity.getWalletId());
        return response;
    }
}
