package com.test.backend.infrastructure.dto;

public record ErrorDto(
    String code,
    String message
) {}
