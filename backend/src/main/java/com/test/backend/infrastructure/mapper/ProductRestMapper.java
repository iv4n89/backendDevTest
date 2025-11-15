package com.test.backend.infrastructure.mapper;

import java.util.List;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.infrastructure.dto.ProductResponse;

public class ProductRestMapper {
    private ProductRestMapper() {
    }

    public static ProductResponse toProductResponse(ProductDetail productDetail) {
        return new ProductResponse(
                productDetail.id(),
                productDetail.name(),
                productDetail.price(),
                productDetail.availability());
    }

    public static ProductDetail toDomainProductDetail(ProductResponse dto) {
        return ProductDetail.builder()
                .id(dto.id())
                .name(dto.name())
                .price(dto.price())
                .availability(dto.availability())
                .build();
    }

    public static List<ProductDetail> toDomainProductDetailList(List<ProductResponse> dtos) {
        return dtos.stream()
                .map(ProductRestMapper::toDomainProductDetail)
                .toList();
    }

    public static List<ProductResponse> toProductResponseList(List<ProductDetail> productDetails) {
        return productDetails.stream()
                .map(ProductRestMapper::toProductResponse)
                .toList();
    }
}
