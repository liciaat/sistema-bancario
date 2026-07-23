package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.AccountRequest;
import br.com.ufca.sixsevenpayapi.domain.entity.CreditRequest;
import br.com.ufca.sixsevenpayapi.domain.entity.CreditRequest;
import br.com.ufca.sixsevenpayapi.domain.entity.Request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RequestResponseDTO(
        Long id,
        Long customerId,
        String customerName,
        String requestType,
        String requestedAccountType,
        BigDecimal requestedLimit,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RequestResponseDTO fromEntity(Request request) {
        String accountTypeStr = null;
        BigDecimal limit = null;

        if (request instanceof AccountRequest accountReq &&  accountReq.getRequestedAccountType() != null) {
            accountTypeStr = accountReq.getRequestedAccountType().name();
        } else if (request instanceof CreditRequest creditReq) {
            limit = creditReq.getRequestedLimit();
        }

        return new RequestResponseDTO(
                request.getId(),
                request.getCustomer() != null ? request.getCustomer().getId() : null,
                request.getCustomer() != null ? request.getCustomer().getName() : null,
                request.getClass().getSimpleName(),
                accountTypeStr,
                limit,
                request.getStatus().name(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}