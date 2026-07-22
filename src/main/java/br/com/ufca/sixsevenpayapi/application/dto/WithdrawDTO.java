package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawDTO(@NotBlank(message = "O número da conta é obrigatório")
                          String accountNumber,
                          @NotNull(message = "O valor é obrigatório")
                          @Positive(message = "O valor do saque deve ser maior que zero")
                          BigDecimal amount) {
}
