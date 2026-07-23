package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ToggleAccountBlockDTO (@Valid @NotBlank(message = "O id da conta é obrigatório")
                                    String accountId){
}
