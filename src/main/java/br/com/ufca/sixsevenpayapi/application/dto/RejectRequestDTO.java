package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RejectRequestDTO(@NotBlank(message = "O motivo da recusa é obrigatório") String reason) {}