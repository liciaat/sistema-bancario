package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferDTO(@NotBlank(message = "O numero da conta destino é obrigatório") String targetAccountNumber,
                          @Valid @NotNull(message = "A senha de transação é obrigatória")
                          String transactionPassword,
                          @NotNull(message = "O valor é obrigatório")
                          @Positive(message = "O valor da transferência deve ser maior que zero")
                          BigDecimal amount) {
}
