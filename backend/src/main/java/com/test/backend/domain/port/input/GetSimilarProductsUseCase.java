package com.test.backend.domain.port.input;

import java.util.List;

import com.test.backend.domain.model.ProductDetail;

public interface GetSimilarProductsUseCase {
    List<ProductDetail> execute(String productId);
}
