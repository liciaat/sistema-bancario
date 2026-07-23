package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageResponseDTO(@NotBlank(message = "A mensagem é obrigatória") String message) {
}
