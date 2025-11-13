package com.test.backend.application.dto;

public record ProductDetailDto(
        String id,
        String name,
        double price,
        boolean availability) {
}
