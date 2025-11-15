package com.test.backend.infrastructure.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.test.backend.domain.exception.ExternalServiceException;
import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.infrastructure.dto.ProductResponse;
import com.test.backend.infrastructure.mapper.ProductRestMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductApiClient implements ProductPort {

    private final RestClient restClient;

    @Value("${api.product.base-url}")
    private String baseUrl;

    @Override
    @CircuitBreaker(name = "productApi", fallbackMethod = "getProductByIdFallback")
    @Cacheable(value = "productDetails", key = "#productId")
    public Optional<ProductDetail> getProductById(String productId) {
        log.debug("Fetching product from external API: {}", productId);

        try {
            ProductResponse response = restClient.get()
                    .uri(baseUrl + "/product/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);

            if (response == null) {
                log.warn("Product API returned null for productId: {}", productId);
                return Optional.empty();
            }

            ProductDetail product = ProductRestMapper.toDomainProductDetail(response);

            return Optional.of(product);
        } catch (HttpClientErrorException.NotFound ex) {
            log.debug("Product not found (404): {}", productId);
            return Optional.empty();
        } catch (Exception ex) {
            log.error("Error fetching product from external API: {}", productId, ex);
            throw new ExternalServiceException("Failed to fetch product: " + productId, ex);
        }

    }

    private Optional<ProductDetail> getProductByIdFallback(String productId, Throwable ex) {
        log.warn("Fallback triggered for product {}: {}", productId, ex.getMessage());
        return Optional.empty();
    }

}
