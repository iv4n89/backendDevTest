package com.test.backend.application.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;

@DisplayName("ProductDetailDto Test")
public class ProductDetailDtoTest {
    @Test
    @DisplayName("Test ProductDetailDto Creation")
    void testProductDetailDtoCreation() {
        ProductDetailDto dto = new ProductDetailDto("1", "Product1", BigDecimal.valueOf(99.99), true);
        
        assertEquals("1", dto.id());
        assertEquals("Product1", dto.name());
        assertEquals(99.99, dto.price().doubleValue());
        assertTrue(dto.availability());
    }
}
