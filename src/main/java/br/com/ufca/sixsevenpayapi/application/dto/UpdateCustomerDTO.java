package br.com.ufca.sixsevenpayapi.application.dto;

/**
 * DTO para atualização parcial de dados do cliente.
 * Campos podem ser nulos — apenas os não-nulos serão atualizados.
 */
public record UpdateCustomerDTO(String name, String email, String phone) {
}
