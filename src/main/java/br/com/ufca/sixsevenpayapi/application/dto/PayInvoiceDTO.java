package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayInvoiceDTO(@Valid @NotNull(message = "O id da fatura é obrigatorio") Long invoiceId,
                            @Valid @NotBlank(message = "A senha de transação é obrigatória")
                            String transactionPassword,
                            @Valid @NotBlank(message = "O numero da conta é obrigatório") String accountNumber) {
}
