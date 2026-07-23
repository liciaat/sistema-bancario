package br.com.ufca.sixsevenpayapi.domain.utils;

import java.security.SecureRandom;

public class CvvGenerator {
        private static final SecureRandom secureRandom = new SecureRandom();

        public static String generateCvv() {
            int cvv = secureRandom.nextInt(1000);
            return String.format("%03d", cvv);
        }
}
