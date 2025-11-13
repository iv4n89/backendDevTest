package com.test.backend.domain.exception;

public class ProductNotFoundException extends RuntimeException {
    private final String productId;

    public ProductNotFoundException(String productId) {
        super(String.format("Product with id '%s' not found", productId));
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }
}
