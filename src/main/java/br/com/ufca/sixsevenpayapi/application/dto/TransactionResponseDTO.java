package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDTO(
        Long id,
        String accountNumber,
        String customerName,
        BigDecimal amount,
        String type,
        LocalDateTime createdAt
) {

    public static TransactionResponseDTO fromEntity(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getAccount().getCustomer().getName(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getCreatedAt()
                );
    }

}
