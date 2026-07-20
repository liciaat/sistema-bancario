package br.com.ufca.sixsevenpayapi.domain.utils;
public class CpfValidator {

    public static String validateAndSanitizeCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("O CPF fornecido não pode ser nulo.");
        }

        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }

        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            throw new IllegalArgumentException("CPF inválido: não pode ser formado por uma sequência de números iguais.");
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpfLimpo.charAt(i) - '0') * (10 - i);
        }

        int resto = 11 - (soma % 11);
        int digito1 = (resto >= 10) ? 0 : resto;

        if (digito1 != (cpfLimpo.charAt(9) - '0')) {
            throw new IllegalArgumentException("CPF inválido: falha na validação do primeiro dígito verificador.");
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpfLimpo.charAt(i) - '0') * (11 - i);
        }

        resto = 11 - (soma % 11);
        int digito2 = (resto >= 10) ? 0 : resto;

        if (digito2 != (cpfLimpo.charAt(10) - '0')) {
            throw new IllegalArgumentException("CPF inválido: falha na validação do segundo dígito verificador.");
        }

        return cpfLimpo;
    }
}
