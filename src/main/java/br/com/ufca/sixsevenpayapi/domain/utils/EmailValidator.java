package br.com.ufca.sixsevenpayapi.domain.utils;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern BASIC_EMAIL_PATTERN =
            Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    public static String validateAndSanitizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        }

        String cleanEmail = email.trim().toLowerCase();

        if (!BASIC_EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("E-mail em formato inválido.");
        }

        return cleanEmail;
    }
}