package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteOwnAccountRequestDTO (
        @NotNull(message = "O id do usuário é obrigatório") Long userId,
        @NotNull(message = "A senha é obrigatória") String password){
}
