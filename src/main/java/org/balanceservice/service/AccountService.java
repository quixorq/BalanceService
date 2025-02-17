package org.balanceservice.service;

import org.balanceservice.dto.AccountDTO;
import org.balanceservice.dto.BalanceResponseDTO;
import org.balanceservice.dto.DepositRequestDTO;
import org.balanceservice.dto.TransferRequestDTO;
import org.balanceservice.dto.WithdrawalRequestDTO;

public interface AccountService {

    AccountDTO createAccount();

    BalanceResponseDTO deposit(DepositRequestDTO request);

    BalanceResponseDTO processWithdrawal(WithdrawalRequestDTO request);

    BalanceResponseDTO processTransfer(TransferRequestDTO request);

    BalanceResponseDTO getBalance(Long walletId);

}
