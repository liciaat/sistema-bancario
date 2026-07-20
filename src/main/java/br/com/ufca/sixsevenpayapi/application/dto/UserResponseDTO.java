package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.User;

public record UserResponseDTO(Long id, String name, String email, String cpf, String role) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getCpf(),
                user.getClass().getSimpleName().toUpperCase() // Ex: CUSTOMER, MANAGER, ADMINISTRATOR
        );
    }
}