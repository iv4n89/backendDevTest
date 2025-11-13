package com.test.backend.domain.port.output;

import java.util.Optional;

import com.test.backend.domain.model.ProductDetail;

public interface ProductPort {
    Optional<ProductDetail> getProductById(String productId);
}
