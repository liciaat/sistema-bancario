package br.com.ufca.sixsevenpayapi.domain.enums;

public enum RequestType {

    ACCOUNT("Abertura de Conta"),
    CREDIT("Solicitação de Crédito"),
    CLOSURE("Encerramento de Conta");

    private final String description;

    RequestType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}