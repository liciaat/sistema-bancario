package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.CreditCard;

import java.math.BigDecimal;

public record CreditCardResponseDTO(
        Long id,
        String carNumber,
        String cvv,
        BigDecimal creditLimit,
        BigDecimal currentSpending,
        BigDecimal avaibleLimit
) {
    public static CreditCardResponseDTO fromEntity(CreditCard creditCard) {
        return new CreditCardResponseDTO(
                creditCard.getId(),
                creditCard.getCardNumber(),
                creditCard.getCvv(),
                creditCard.getCreditLimit(),
                creditCard.getCurrentSpending(),
                creditCard.getCreditLimit().subtract(creditCard.getCurrentSpending())
        );
    }

}
