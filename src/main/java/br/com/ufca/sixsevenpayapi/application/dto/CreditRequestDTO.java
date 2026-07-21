package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreditRequestDTO(@NotNull(message = "O ID do cliente é obrigatório") Long customerId,
                               @NotNull(message = "O limite solicitado é obrigatório")
                               @Positive(message = "O limite deve ser maior que zero")
                               BigDecimal requestedLimit) {
}
