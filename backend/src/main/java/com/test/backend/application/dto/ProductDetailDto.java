package com.test.backend.application.dto;

import java.math.BigDecimal;

public record ProductDetailDto(
        String id,
        String name,
        BigDecimal price,
        boolean availability) {
}
