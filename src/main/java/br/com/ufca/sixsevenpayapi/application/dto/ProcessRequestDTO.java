package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProcessRequestDTO(@Valid @NotNull(message = "O id da solicitação é obrigatório") Long requestId,
                                @Valid @NotNull(message = "O id do gerente é obrigatório") Long managerId,
                                @Valid @NotBlank(message = "A senha do gerente  é obrigatória") String managerPassword) {
}
