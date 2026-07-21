package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotNull;

public record SavingsAccountRequestDTO(@NotNull(message = "O ID do cliente é obrigatório") Long customerId) {
}
