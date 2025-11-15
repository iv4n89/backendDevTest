package com.test.backend.infrastructure.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.test.backend.domain.exception.ExternalApiException;
import com.test.backend.domain.exception.ProductNotFoundException;
import com.test.backend.infrastructure.dto.ErrorDto;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleProductNotFound(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getMessage());

        return new ErrorDto(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                String.valueOf(Instant.now()));
    }

    @ExceptionHandler(ExternalApiException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDto handleExternalApiException(ExternalApiException ex) {
        log.error("External API error: {}", ex.getMessage());

        return new ErrorDto(
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                String.valueOf(Instant.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        return new ErrorDto(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Invalid request parameters",
                HttpStatus.BAD_REQUEST.value(),
                String.valueOf(Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                String.valueOf(Instant.now()));
    }
}
