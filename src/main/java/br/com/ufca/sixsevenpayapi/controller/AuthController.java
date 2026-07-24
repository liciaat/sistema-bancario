package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.application.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticação",
        description = "Operações de login, registro e gerenciamento de credenciais"
)

public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Realizar login",
            description = "Autentica um usuário (Cliente, Gerente ou Admin) e retorna seus dados básicos."
    )
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        UserResponseDTO response = authenticationService.login(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registrar novo cliente",
            description = "Cadastra um novo cliente no sistema e abre uma solicitação de conta corrente inicial."
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        UserResponseDTO response = authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Atualizar senha",
            description = "Permite que o usuário altere sua senha de acesso mediante validação da senha atual."
    )
    @PatchMapping("/update-password")
    public ResponseEntity<MessageResponseDTO> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto){
        authenticationService.updatePassword(dto);
        return ResponseEntity.ok(new MessageResponseDTO("Senha atualizada com sucesso"));
    }

    @Operation(
            summary = "Solicitar encerramento de conta",
            description = "Clientes solicitam o encerramento (criando uma solicitação pendente). Administradores ou Gerentes são inativados ou bloqueados conforme a regra de negócio."
    )
    @PostMapping("/close-acount")
    public ResponseEntity<MessageResponseDTO> deleteOwnAccount(@Valid @RequestBody DeleteOwnAccountRequestDTO dto){
        authenticationService.deleteOwnAccount(dto);
        return ResponseEntity.ok(new MessageResponseDTO("Solicitação de encerramento enviada para a gerência"));
    }


}
