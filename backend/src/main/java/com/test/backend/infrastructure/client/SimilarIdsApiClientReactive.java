package com.test.backend.infrastructure.client;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.test.backend.domain.exception.ExternalApiException;
import com.test.backend.domain.exception.ProductNotFoundException;
import com.test.backend.domain.port.output.SimilarIdsPort;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarIdsApiClientReactive implements SimilarIdsPort {

    private final WebClient webClient;

    @Value("${api.product.base-url}")
    private String baseUrl;

    @Override
    @Cacheable(value = "similarIds", key = "#productId", unless = "#result.isEmpty()")
    @CircuitBreaker(name = "similarIdsApi", fallbackMethod = "getSimilarProductIdsFallback")
    public List<String> getSimilarProductIds(String productId) {
        log.debug("Fetching similar IDs from external API for: {}", productId);
        return getSimilarProductIdsReactive(productId).block();
    }

    private List<String> getSimilarProductIdsFallback(String productId, Exception ex) {
        log.error("Fallback triggered for similar IDs: {}", productId, ex);
        if (ex instanceof ProductNotFoundException || ex instanceof ExternalApiException)
            throw (RuntimeException) ex;
        throw new ExternalApiException(
                "Similar products API is unavailable for product: " + productId, ex);
    }

    private Mono<List<String>> getSimilarProductIdsReactive(String productId) {
        return webClient.get()
                .uri(baseUrl + "/product/{id}/similarids", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.debug("Similar IDs not found for: {}", productId);
                    return Mono.error(new ProductNotFoundException(productId));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn("Error fetching similar IDs from external API for: {}", productId, ex);
                    return Mono.error(new ExternalApiException(
                            "Similar products API is unavailable for product: " + productId, ex));
                });
    }
}