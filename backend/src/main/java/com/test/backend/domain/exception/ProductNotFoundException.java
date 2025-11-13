package com.test.backend.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super(String.format("Product with id '%s' not found", productId));
    }
}
