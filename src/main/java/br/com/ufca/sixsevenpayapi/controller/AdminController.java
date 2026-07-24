package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.DashboardResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.MessageResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
    @PostMapping("/managers")
    public ResponseEntity<UserResponseDTO> registerManager(@RequestBody RegisterManagerDTO dto){
        UserResponseDTO response = adminService.registerManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Remover um gerente",
            description = "Desativa permanentemente a conta de um gerente através do seu ID."
    )
    @DeleteMapping("/RemoveManagers/{id}")
    public ResponseEntity<MessageResponseDTO> removeManager(@PathVariable Long id){
        adminService.removeManager(id);
        return ResponseEntity.ok(new MessageResponseDTO("Conta de gerente desativada"));
    }

    @Operation(
            summary = "Métricas do Dashboard",
            description = "Retorna os dados globais do banco: total em caixa, clientes totais, contas bloqueadas, etc."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDTO> getDashboard(){
        DashboardResponseDTO response = adminService.getDashboardMetrics();
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Atualizar taxa de juros",
            description = "Define a nova taxa de rendimento mensal que será aplicada nas Contas Poupança."
    )
    @PostMapping("/interest-rate")
    public ResponseEntity<MessageResponseDTO> updateInterestRate(@RequestParam BigDecimal rate){
        adminService.updateSavingsInterestRate(rate);
        return ResponseEntity.ok(new MessageResponseDTO("Taxa de juros atuaizada com sucesso"));
    }

}
