package br.com.ufca.sixsevenpayapi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(@NotBlank(message = "O nome é obrigatório") String name,
                                 @NotBlank(message = "O cpf é obrigatório") String cpf,
                                 @NotBlank(message = "A senha é obrigatório") String password,
                                 @NotBlank(message = "A confirmação da senha é obrigatório") String confirmPassword,
                                 @Valid @NotBlank(message = "A senha de transação é obrigatória")
                                 String transactionPassword,
                                 @Valid @NotBlank(message = "A confirmação da senha de transação é obrigatória")
                                 String confirmTransactionPassword,
                                 @NotBlank(message = "O email é obrigatório") String email,
                                 @NotBlank(message = "O numero é obrigatório") String phoneNumber) {
}
