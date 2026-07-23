package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InvoiceDTO (@Valid
                         @NotNull(message = "O id da fatura é obrigatório")
                         long invoiceId){
}
