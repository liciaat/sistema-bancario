package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.DepositDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransferDTO;
import br.com.ufca.sixsevenpayapi.application.dto.WithdrawDTO;
import br.com.ufca.sixsevenpayapi.application.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDTO> deposit(@Valid @RequestBody DepositDTO dto){
        TransactionResponseDTO response =  accountService.deposit(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawDTO dto){
        TransactionResponseDTO response =  accountService.withdraw(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferDTO dto){
        TransactionResponseDTO response =  accountService.transfer(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountNumber}/transactionHistory")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionHistory(@PathVariable String accountNumber){
        List<TransactionResponseDTO> response =  accountService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(response);
    }



}
