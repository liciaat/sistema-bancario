package br.com.ufca.sixsevenpayapi.application.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorDTO(
        LocalDateTime timestamp,
        Integer status,
        String error,
        Map<String,String> fieldErrors,
        String path
) {
}
