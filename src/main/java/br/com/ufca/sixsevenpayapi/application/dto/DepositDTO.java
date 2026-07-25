package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositDTO(@NotNull(message = "O valor é obrigatório")
                         @Positive(message = "O valor do depósito deve ser maior que zero")
                         BigDecimal amount) {}
