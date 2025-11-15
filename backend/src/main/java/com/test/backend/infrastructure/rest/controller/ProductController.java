package com.test.backend.infrastructure.rest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.infrastructure.dto.ProductResponse;
import com.test.backend.infrastructure.mapper.ProductRestMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Tag(name = "Product Controller", description = "APIs for product management")
public class ProductController {
    private final GetSimilarProductsUseCase getSimilarProductsUseCase;

    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponse>> getSimilarProducts(
            @NotBlank @PathVariable String productId) {
        log.info("GET /product/{}/similar called", productId);

        List<ProductDetail> products = getSimilarProductsUseCase.execute(productId);
        List<ProductResponse> response = ProductRestMapper.toProductResponseList(products);

        return ResponseEntity.ok(response);
    }
}
