package com.test.backend.infrastructure.client;

import com.test.backend.domain.port.output.SimilarIdsPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

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
        return List.of();
    }

    private Mono<List<String>> getSimilarProductIdsReactive(String productId) {
        return webClient.get()
                .uri(baseUrl + "/product/{id}/similarids", productId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .map(list -> list != null ? list : List.<String>of())
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
                    log.warn("Product not found when fetching similar IDs: {}", productId);
                    return Mono.just(List.of());
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Error fetching similar IDs from external API: {}", productId, ex);
                    return Mono.just(List.of());
                });
    }
}