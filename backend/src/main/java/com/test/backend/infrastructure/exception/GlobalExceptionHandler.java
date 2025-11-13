package com.test.backend.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.test.backend.domain.exception.ExternalServiceException;
import com.test.backend.domain.exception.ProductNotFoundException;
import com.test.backend.infrastructure.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorDto handleProductNotFoundException(ProductNotFoundException ex) {
        log.error("Product not found: {}", ex.getProductId());

        return new ErrorDto(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "Product with ID " + ex.getProductId() + " not found.");
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ErrorDto handleExternalServiceException(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage());

        return new ErrorDto(
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "External service error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ErrorDto handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage());
    }
}
