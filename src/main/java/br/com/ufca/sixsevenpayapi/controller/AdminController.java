package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.DashboardResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.MessageResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/managers")
    public ResponseEntity<UserResponseDTO> registerManager(@RequestBody RegisterManagerDTO dto){
        UserResponseDTO response = adminService.registerManager(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/RemoveManagers/{id}")
    public ResponseEntity<MessageResponseDTO> removeManager(@PathVariable Long id){
        adminService.removeManager(id);
        return ResponseEntity.ok(new MessageResponseDTO("Conta de gerente desativada"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponseDTO> getDashboard(){
        DashboardResponseDTO response = adminService.getDashboardMetrics();
        return ResponseEntity.ok(response);
    }

}
