package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.application.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        UserResponseDTO response = authenticationService.login(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        UserResponseDTO response = authenticationService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/update-password")
    public ResponseEntity<MessageResponseDTO> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto){
        authenticationService.updatePassword(dto);
        return ResponseEntity.ok(new MessageResponseDTO("Senha atualizada com sucesso"));
    }

    @PostMapping("/close-acount")
    public ResponseEntity<MessageResponseDTO> deleteOwnAccount(@Valid @RequestBody DeleteOwnAccountRequestDTO dto){
        authenticationService.deleteOwnAccount(dto);
        return ResponseEntity.ok(new MessageResponseDTO("Solicitação de encerramento enviada para a gerência"));
    }


}
