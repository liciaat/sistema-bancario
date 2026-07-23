package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PurchaseDTO(@Valid @NotBlank(message = "O numero do cartão é obrigatório")
                          String cardNumber,
                          @Valid @NotBlank(message = "O cvv do cartão é obrigatório")
                          String cvv,
                          @NotNull(message = "O valor é obrigatório")
                          @Positive(message = "O valor da transferência deve ser maior que zero")
                          BigDecimal amount,
                          String description
) {
    @Override
    public String description() {
        if(description == null){
            return "Sem descrição";
        }
        return description;
    }

}
