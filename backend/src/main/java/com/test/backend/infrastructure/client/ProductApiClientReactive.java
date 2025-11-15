package com.test.backend.infrastructure.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.infrastructure.dto.ProductResponse;
import com.test.backend.infrastructure.mapper.ProductRestMapper;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductApiClientReactive implements ProductPort {

    private final WebClient webClient;

    @Value("${api.product.base-url}")
    private String baseUrl;

    @Override
    @Cacheable(value = "productDetails", key = "#productId")
    @CircuitBreaker(name = "productApi", fallbackMethod = "getProductByIdFallback")
    public Optional<ProductDetail> getProductById(String productId) {
        log.debug("Fetching product from external API: {}", productId);
        return getProductByIdReactive(productId).block();
    }

    private Optional<ProductDetail> getProductByIdFallback(String productId, Exception ex) {
        log.error("Fallback triggered for product: {}", productId, ex);
        return Optional.empty();
    }

    private Mono<Optional<ProductDetail>> getProductByIdReactive(String productId) {
        return webClient.get()
                .uri(baseUrl + "/product/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .map(response -> Optional.of(ProductRestMapper.toDomainProductDetail(response)))
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.debug("Product not found 404: {}", productId);
                    return Mono.just(Optional.empty());
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching product from external API: {}", productId, ex);
                    return Mono.just(Optional.empty());
                });
    }

}
