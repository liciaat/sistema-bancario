package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.AccountRequest;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;

import java.time.LocalDateTime;

public record AccountRequestResponseDTO(
        Long id,
        String name,
        String cpf,
        AccountType accountType,
        RequestStatus status,
        LocalDateTime createdAt
) {
    public static AccountRequestResponseDTO fromEntity(AccountRequest request) {
        return new AccountRequestResponseDTO(
                request.getId(),
                request.getCustomer().getName(),
                request.getCustomer().getCpf(),
                request.getRequestedAccountType(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }

}
