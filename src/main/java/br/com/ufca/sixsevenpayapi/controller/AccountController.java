package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.DepositDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransferDTO;
import br.com.ufca.sixsevenpayapi.application.dto.WithdrawDTO;
import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.ufca.sixsevenpayapi.application.dto.StandardErrorDTO;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(
        name = "Contas",
        description = "Operações de movimentação financeira das contas"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Realiza um depósito",
            description = "Adiciona um valor ao saldo da conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Depósito realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@PathVariable Long accountId, @Valid @RequestBody DepositDTO dto){
        TransactionResponseDTO response =  accountService.deposit(accountId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Realiza um saque",
            description = "Remove um valor do saldo da conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saque realizado"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@PathVariable Long accountId, @Valid @RequestBody WithdrawDTO dto){
        TransactionResponseDTO response =  accountService.withdraw(accountId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Realiza uma transferência",
            description = "Transfere dinheiro entre duas contas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência realizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PostMapping("/{sourceAccountId}/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@PathVariable Long sourceAccountId, @Valid @RequestBody TransferDTO dto){
        TransactionResponseDTO response =  accountService.transfer(sourceAccountId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consulta o histórico",
            description = "Lista todas as transações de uma conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/{accountId}/transactionHistory")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable Long accountId){
        List<TransactionResponseDTO> response =  accountService.getTransactionHistory(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Consultar conta por número", description = "Retorna a conta correspondente ao número informado")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByNumber(@PathVariable String accountNumber){
        AccountResponseDTO response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }


}
