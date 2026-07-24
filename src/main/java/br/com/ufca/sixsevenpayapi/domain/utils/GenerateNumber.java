package br.com.ufca.sixsevenpayapi.domain.utils;

import java.security.SecureRandom;

public class GenerateNumber {

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateAccountNumber() {
        int number = secureRandom.nextInt(1_000_000);
        return String.format("%06d", number);
    }

    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}