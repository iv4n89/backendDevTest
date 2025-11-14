package com.test.backend.infrastructure.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.test.backend.domain.exception.ExternalServiceException;
import com.test.backend.infrastructure.dto.ErrorDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorDto handleExternalServiceException(ExternalServiceException ex) {
        log.error("External service error: {}", ex.getMessage());

        return new ErrorDto(
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "External service error: " + ex.getMessage(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
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
