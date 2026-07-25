package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.DashboardResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.MessageResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UpdateManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.ufca.sixsevenpayapi.application.dto.StandardErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@Tag(
        name = "Administração",
        description = "Operações exclusivas do Administrador Geral do banco"
)
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @Operation(
            summary = "Registrar um gerente",
            description = "Adiciona um novo gerente ao sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Gerente criado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito: CPF/Email já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PostMapping("/managers")
    public ResponseEntity<UserResponseDTO> registerManager(@RequestBody RegisterManagerDTO dto){
        UserResponseDTO response = adminService.registerManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Remover um gerente",
            description = "Desativa permanentemente a conta de um gerente através do seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerente removido"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @DeleteMapping("/managers/{id}")
    public ResponseEntity<MessageResponseDTO> removeManager(@PathVariable Long id){
        adminService.removeManager(id);
        return ResponseEntity.ok(new MessageResponseDTO("Conta de gerente desativada"));
    }

    @Operation(
            summary = "Métricas do Dashboard",
            description = "Retorna os dados globais do banco: total em caixa, clientes totais, contas bloqueadas, etc."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retornado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDTO> getDashboard(){
        DashboardResponseDTO response = adminService.getDashboardMetrics();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar taxa de juros",
            description = "Define a nova taxa de rendimento mensal que será aplicada nas Contas Poupança."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Taxa atualizada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PostMapping("/interest-rate")
    public ResponseEntity<MessageResponseDTO> updateInterestRate(@RequestParam BigDecimal rate){
        adminService.updateSavingsInterestRate(rate);
        return ResponseEntity.ok(new MessageResponseDTO("Taxa de juros atuaizada com sucesso"));
    }

    @Operation(summary = "Obter gerente pelo ID", description = "Retorna os dados de um gerente pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerente retornado"),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/managers/{id}")
    public ResponseEntity<UserResponseDTO> getManagerById(@PathVariable Long id){
        UserResponseDTO response = adminService.getManagerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar todos os gerentes", description = "Retorna todos os gerentes cadastrados no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerentes retornados"),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/managers")
    public ResponseEntity<List<UserResponseDTO>> getAllManagers(){
        List<UserResponseDTO> response = adminService.getAllManagers();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar gerente", description = "Atualiza campos flexíveis de um gerente (nome, email, telefone)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gerente atualizado"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Gerente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PatchMapping("/managers/{id}")
    public ResponseEntity<UserResponseDTO> updateManager(@PathVariable Long id, @Valid @RequestBody UpdateManagerDTO dto){
        UserResponseDTO response = adminService.updateManager(id, dto);
        return ResponseEntity.ok(response);
    }

}
