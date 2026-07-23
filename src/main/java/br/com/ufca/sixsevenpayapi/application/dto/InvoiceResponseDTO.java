package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.Invoice;
import br.com.ufca.sixsevenpayapi.domain.entity.Purchase;
import br.com.ufca.sixsevenpayapi.domain.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record InvoiceResponseDTO(
        Long id,
        String status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<PurchaseResponseDTO> purchases
) {
    public static InvoiceResponseDTO fromEntity(Invoice invoice) {
        List<Purchase> purchases = invoice.getPurchases();
        List<PurchaseResponseDTO> purchasesDTO = new ArrayList<>();
        for (Purchase purchase : purchases) {
            purchasesDTO.add(PurchaseResponseDTO.fromEntity(purchase));
        }

        return new InvoiceResponseDTO(
                invoice.getId(),
                invoice.getStatus().name(),
                invoice.getTotalAmount(),
                invoice.getCreatedAt(),
                purchasesDTO
        );
    }

}
