package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.Purchase;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PurchaseResponseDTO(
        Long id,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
    public static PurchaseResponseDTO fromEntity(Purchase purchase) {
        return new PurchaseResponseDTO(
                purchase.getId(),
                purchase.getAmount(),
                purchase.getDescription(),
                purchase.getCreatedAt()
        );
    }
}