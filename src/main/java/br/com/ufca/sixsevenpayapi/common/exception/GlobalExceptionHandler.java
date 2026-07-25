package br.com.ufca.sixsevenpayapi.common.exception;

import br.com.ufca.sixsevenpayapi.application.dto.StandardErrorDTO;
import br.com.ufca.sixsevenpayapi.application.dto.ValidationErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

     @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardErrorDTO> handleNotFound(NotFoundException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Recurso não encontrado", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardErrorDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Requisição inválida", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<StandardErrorDTO> handleConflict(ConflictException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.CONFLICT;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Conflito", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardErrorDTO> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Não autorizado", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardErrorDTO> handleForbidden(ForbiddenException ex, HttpServletRequest request){
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Proibido", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, String> fieldErrors = new HashMap<>();
        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ValidationErrorDTO error = new ValidationErrorDTO(LocalDateTime.now(), status.value(), "Erro de validação nos campos", fieldErrors, request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardErrorDTO> handleGeneric(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        StandardErrorDTO error = new StandardErrorDTO(LocalDateTime.now(), status.value(), "Erro interno", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

}
