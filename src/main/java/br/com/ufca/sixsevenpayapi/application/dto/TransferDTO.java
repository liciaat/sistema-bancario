package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferDTO(@NotNull(message = "O id da conta destino é obrigatório") Long targetAccountId,
                          @Valid @NotNull(message = "A senha de transação é obrigatória")
                          String transactionPassword,
                          @NotNull(message = "O valor é obrigatório")
                          @Positive(message = "O valor da transferência deve ser maior que zero")
                          BigDecimal amount) {
}
