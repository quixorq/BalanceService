package org.balanceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balanceservice.dto.AccountDTO;
import org.balanceservice.dto.BalanceResponseDTO;
import org.balanceservice.dto.DepositRequestDTO;
import org.balanceservice.dto.ErrorResponse;
import org.balanceservice.dto.TransactionHistoryRequestDTO;
import org.balanceservice.dto.TransactionHistoryResponseDTO;
import org.balanceservice.dto.TransferRequestDTO;
import org.balanceservice.dto.WithdrawalRequestDTO;
import org.balanceservice.service.AccountService;
import org.balanceservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/accounts")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Operation(summary = "Создание нового счета")
    @PostMapping("/create")
    public ResponseEntity<AccountDTO> createAccount() {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount());
    }

    @Operation(summary = "Зачисление средств на счет", description = "Позволяет зачислить средства на указанный счет")
    @ApiResponse(responseCode = "200", description = "Средства успешно зачислены")
    @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(path = "/deposit")
    public ResponseEntity<BalanceResponseDTO> deposit(@RequestBody @Valid DepositRequestDTO request) {
        return ResponseEntity.ok(accountService.deposit(request));
    }

    @Operation(summary = "Списание средств со счета", description = "Позволяет списать средства с указанного счета")
    @ApiResponse(responseCode = "200", description = "Средства успешно списаны", content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Счет не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(path = "/withdraw")
    public ResponseEntity<BalanceResponseDTO> withdraw(@RequestBody @Valid WithdrawalRequestDTO request) {
        return ResponseEntity.ok(accountService.processWithdrawal(request));
    }

    @Operation(summary = "Перевод средств между счетами", description = "Позволяет перевести средства с одного счета на другой")
    @ApiResponse(responseCode = "200", description = "Средства успешно переведены", content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Один из счетов не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(path = "/transfer")
    public ResponseEntity<BalanceResponseDTO> transfer(@RequestBody @Valid TransferRequestDTO request) {
        return ResponseEntity.ok(accountService.processTransfer(request));
    }

    @Operation(summary = "Получение баланса счета", description = "Возвращает текущий баланс указанного счета")
    @ApiResponse(responseCode = "200", description = "Баланс успешно получен", content = @Content(schema = @Schema(implementation = BalanceResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Счет не найден", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(path = "/{walletId}/balance")
    public ResponseEntity<BalanceResponseDTO> getBalance(
            @Parameter(description = "Идентификатор счета", example = "1", required = true)
            @PathVariable Long walletId) {
        return ResponseEntity.ok(accountService.getBalance(walletId));
    }

    @Operation(summary = "Получение выписки по транзакциям", description = "Возвращает список транзакций за указанный период")
    @ApiResponse(responseCode = "200", description = "Выписка успешно получена", content = @Content(schema = @Schema(implementation = TransactionHistoryResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(path = "/{walletId}/transactions")
    public ResponseEntity<TransactionHistoryResponseDTO> getTransactionHistory(
            @Parameter(description = "Идентификатор счета", example = "1", required = true)
            @PathVariable Long walletId,
            @Parameter(description = "Дата начала периода", example = "2023-01-01T00:00:00", required = true)
            @RequestParam LocalDateTime startDate,
            @Parameter(description = "Дата окончания периода", example = "2023-12-31T23:59:59", required = true)
            @RequestParam LocalDateTime endDate) {

        TransactionHistoryRequestDTO request = new TransactionHistoryRequestDTO();
        request.setWalletId(walletId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        return ResponseEntity.ok(transactionService.getTransactionHistory(request));
    }

}
