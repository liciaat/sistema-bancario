package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotNull;

public record ProcessRequestDTO(@NotNull Long managerId) {
}
