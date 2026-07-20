package br.com.ufca.sixsevenpayapi.domain.utils;

public class PhoneValidator {

    public static String validateAndSanitizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }

        String digitsOnly = phone.replaceAll("\\D", "");

        if (digitsOnly.length() < 8 || digitsOnly.length() > 15) {
            throw new IllegalArgumentException("Número de telefone em formato inválido.");
        }

        return digitsOnly;
    }
}