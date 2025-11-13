package com.test.backend.application.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductDetailDtoTest {
    @Test
    void testProductDetailDtoCreation() {
        ProductDetailDto dto = new ProductDetailDto("1", "Product1", 99.99, true);
        
        assertEquals("1", dto.id());
        assertEquals("Product1", dto.name());
        assertEquals(99.99, dto.price());
        assertTrue(dto.availability());
    }
}
