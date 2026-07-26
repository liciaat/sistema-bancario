package br.com.ufca.sixsevenpayapi.controller;


import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.application.service.ManagerService;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    public ResponseEntity<List<RequestResponseDTO>> listPendingRequests() {
        List<RequestResponseDTO> response = managerService.listPendingRequests();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter gerente", description = "Retorna os dados do gerente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerente retornado"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/{managerId}")
    public ResponseEntity<UserResponseDTO> getManager(@PathVariable Long managerId){
        UserResponseDTO response = managerService.getManager(managerId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Bloquear/desbloquear conta",
            description = "Altera o status de uma conta entre ATIVA e BLOQUEADA."
    )
    @PatchMapping("/accounts/{accountId}/toggle-status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado"),
            @ApiResponse(responseCode = "400", description = "Operação inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    public ResponseEntity<AccountResponseDTO> toggleAccountStatus(@PathVariable Long accountId) {
        AccountResponseDTO response = managerService.toggleAccountStatus(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Relatório geral de transações",
            description = "Retorna o histórico completo de transações do banco, ordenado pelas mais recentes."
    )
    @GetMapping("/reports/transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório retornado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    public ResponseEntity<List<TransactionResponseDTO>> getGeneralTransactions() {
        List<TransactionResponseDTO> response = managerService.getGeneralTransactions();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Relatório de contas negativadas",
            description = "Retorna uma lista de todas as contas que possuem saldo menor que zero."
    )
    @GetMapping("/reports/negative-accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    public ResponseEntity<List<AccountResponseDTO>> getNegativeAccounts() {
        List<AccountResponseDTO> response =  managerService.getNegativeAccounts();
        return ResponseEntity.ok(response);
    }



    @Operation(
            summary = "Relatório de clientes",
            description = "Retorna uma lista com os dados de todos os clientes cadastados no banco."
    )
    @GetMapping("/reports/customers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> response = managerService.getCustomers();
        return ResponseEntity.ok(response);
    }


}
