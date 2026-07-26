package br.com.ufca.sixsevenpayapi.application.dto;

import java.math.BigDecimal;

public record BalanceResponseDTO(
        Long accountId,
        String accountNumber,
        BigDecimal balance
) {
}
