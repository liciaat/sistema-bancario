package br.com.ufca.sixsevenpayapi.domain.utils;

import java.util.Random;

public class GenerateNumber {

    public static String generateAccountNumber() {
        return String.format("%06d", (int) (Math.random() * 1_000_000));
    }
    public static String generateCardNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}
