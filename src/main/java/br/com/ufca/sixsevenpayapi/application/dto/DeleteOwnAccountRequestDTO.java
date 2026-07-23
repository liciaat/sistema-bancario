package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DeleteOwnAccountRequestDTO (
        @NotBlank(message = "O id da conta é obrigatório") Long accountId,
        @NotNull(message = "A senha é obrigatória") String password){
}
