package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.ToggleAccountBlockDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/managers")
@Tag(
        name = "Gerência",
        description = "Painel gerencial para aprovações de solicitações, bloqueio de contas e relatórios"
)
public class ManagerController {

    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Operation(
            summary = "Listar solicitações pendentes",
            description = "Retorna todas as solicitações (conta, crédito, encerramento) aguardando aprovação."
    )

    @GetMapping("/requests/pending")
    public ResponseEntity<List<RequestResponseDTO>> listPendingRequests() {
        List<RequestResponseDTO> response = managerService.listPendingRequests();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Bloquear/desbloquear conta",
            description = "Altera o status de uma conta entre ATIVA e BLOQUEADA."
    )
    @PatchMapping("/accounts/toggle-status")
    public ResponseEntity<AccountResponseDTO> toggleAccountStatus(@Valid @RequestBody ToggleAccountBlockDTO dto) {
        AccountResponseDTO response = managerService.toggleAccountStatus(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Relatório geral de transações",
            description = "Retorna o histórico completo de transações do banco, ordenado pelas mais recentes."
    )
    @GetMapping("/reports/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getGeneralTransactions() {
        List<TransactionResponseDTO> response = managerService.getGeneralTransactions();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Relatório de contas negativadas",
            description = "Retorna uma lista de todas as contas que possuem saldo menor que zero."
    )
    @GetMapping("/reports/negative-accounts")
    public ResponseEntity<List<AccountResponseDTO>> getNegativeAccounts() {
        List<AccountResponseDTO> response =  managerService.getNegativeAccounts();
        return ResponseEntity.ok(response);
    }

}
