package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterManagerDTO(
        @NotBlank(message = "O nome é obrigatório") String name,
        @NotBlank(message = "O CPF é obrigatório") String cpf,
        @NotBlank(message = "O e-mail é obrigatório") String email,
        @NotBlank(message = "A senha é obrigatória") String password,
        @NotBlank(message = "O telefone é obrigatório") String phone,
        @NotBlank(message = "A matrícula é obrigatória") String registration
) {
}
