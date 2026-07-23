package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.DepositDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransferDTO;
import br.com.ufca.sixsevenpayapi.application.dto.WithdrawDTO;
import br.com.ufca.sixsevenpayapi.application.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositDTO dto){
        TransactionResponseDTO response =  accountService.deposit(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Realiza um saque",
            description = "Remove um valor do saldo da conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saque realizado"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawDTO dto){
        TransactionResponseDTO response =  accountService.withdraw(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Realiza uma transferência",
            description = "Transfere dinheiro entre duas contas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência realizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferDTO dto){
        TransactionResponseDTO response =  accountService.transfer(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consulta o histórico",
            description = "Lista todas as transações de uma conta."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado"),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    })
    @GetMapping("/{accountNumber}/transactionHistory")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable String accountNumber){
        List<TransactionResponseDTO> response =  accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(response);
    }



}
