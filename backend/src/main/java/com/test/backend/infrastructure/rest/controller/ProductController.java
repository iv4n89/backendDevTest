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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get similar products", description = "Retrieves a list of similar products for a given product ID. "
            +
            "Returns products with similar characteristics, filtered by availability. " +
            "Uses caching for improved performance and circuit breaker for resilience.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved similar products. Returns an empty array if no similar products are available or all are unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found. The external API does not have a record of the requested product."),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred processing the request."),
            @ApiResponse(responseCode = "503", description = "Service unavailable. The external product API is currently unavailable or unreachable.")
    })
    @GetMapping("/{productId}/similar")
    public ResponseEntity<List<ProductResponse>> getSimilarProducts(
            @Parameter(description = "Product ID to find similar products for", required = true, example = "1") @NotBlank @PathVariable String productId) {
        log.info("GET /product/{}/similar called", productId);

        List<ProductDetail> products = getSimilarProductsUseCase.execute(productId);
        List<ProductResponse> response = ProductRestMapper.toProductResponseList(products);

        return ResponseEntity.ok(response);
    }
}
