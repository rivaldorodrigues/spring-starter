package com.github.rivaldorodrigues.springstarter.infrastructure;

import com.github.rivaldorodrigues.springstarter.application.exception.ValidationException;
import com.github.rivaldorodrigues.springstarter.infrastructure.service.MessageService;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@ControllerAdvice
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private SystemClock systemClock;
    private MessageService message;

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity handleAccessDeniedException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, message.get("error.access-denied"), new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value = {ValidationException.class})
    public ResponseEntity handleValidationException(ValidationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(ex, createValidationBody(status, ex), new HttpHeaders(), status, request);
    }

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity handleValidationException(ResponseStatusException ex, WebRequest request) {
        HttpStatus status = ex.getStatus();
        return handleExceptionInternal(ex, createResponseStatusBody(status, ex), new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidationException.Builder builder = new ValidationException.Builder();
        ex.getBindingResult().getFieldErrors().forEach(e -> builder.addValidationError(e.getField(), e.getDefaultMessage()));
        return new ResponseEntity<>(createValidationBody(status, builder.build()), headers, status);
    }

    private Map<String, Object> createValidationBody(HttpStatus status, ValidationException ex) {

        Map<String, Object> body = Maps.newHashMap();
        body.put("timestamp", systemClock.currentDate());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("validation", ex.getValidation());
        body.put("message", ex.getMessage());

        return body;
    }

    private Map<String, Object> createResponseStatusBody(HttpStatus status, ResponseStatusException ex) {

        Map<String, Object> body = Maps.newHashMap();
        body.put("timestamp", systemClock.currentDate());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", ex.getReason());

        return body;
    }
}
