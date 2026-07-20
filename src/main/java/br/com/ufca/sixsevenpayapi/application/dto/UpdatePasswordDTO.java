package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordDTO(@NotBlank(message = "O CPF é obrigatório") String cpf,
                                @NotBlank(message = "A senha atual é obrigatória") String password,
                                @NotBlank(message = "A nova senha é obrigatória") String newPassword) {
}
