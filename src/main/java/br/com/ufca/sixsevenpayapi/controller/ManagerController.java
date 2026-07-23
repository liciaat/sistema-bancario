package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.ToggleAccountBlockDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/requests/pending")
    public ResponseEntity<List<RequestResponseDTO>> listPendingRequests() {
        List<RequestResponseDTO> response = managerService.listPendingRequests();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accounts/toggle-status")
    public ResponseEntity<AccountResponseDTO> toggleAccountStatus(@Valid @RequestBody ToggleAccountBlockDTO dto) {
        AccountResponseDTO response = managerService.toggleAccountStatus(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getGeneralTransactions() {
        List<TransactionResponseDTO> response = managerService.getGeneralTransactions();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reports/negative-accounts")
    public ResponseEntity<List<AccountResponseDTO>> getNegativeAccounts() {
        List<AccountResponseDTO> response = managerService.getNegativeAccounts();
        return ResponseEntity.ok(response);
    }

}
