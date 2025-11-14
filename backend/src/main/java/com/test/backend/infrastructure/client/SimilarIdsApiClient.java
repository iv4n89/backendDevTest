package com.test.backend.infrastructure.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.test.backend.domain.exception.ExternalServiceException;
import com.test.backend.domain.port.output.SimilarIdsPort;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimilarIdsApiClient implements SimilarIdsPort {

    private final RestClient restClient;

    @Value("${api.product.base-url}")
    private String baseUrl;

    @Override
    @CircuitBreaker(name = "similarIdsApi", fallbackMethod = "getSimilarIdsFallback")
    @Cacheable(value = "similarIds", key = "#productId", unless = "#result.isEmpty()")
    public List<String> getSimilarProductIds(String productId) {
        log.debug("Fetching similar IDs from external API for: {}", productId);

        try {
            List<String> similarIds = restClient.get()
                    .uri(baseUrl + "/product/{id}/similarids", productId)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {
                    });

            List<String> result = similarIds != null ? similarIds : List.of();

            log.debug("Found {} similar IDs for product: {}", result.size(), productId);
            return result;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Product not found when fetching similar IDs: {}", productId);
            return List.of();
        } catch (Exception ex) {
            log.error("Error fetching similar IDs from external API for: {}", productId, ex);
            throw new ExternalServiceException("Failed to fetch similar IDs for product: " + productId, ex);
        }
    }

    private List<String> getSimilarIdsFallback(String productId, Throwable throwable) {
        log.error("Fallback triggered for getSimilarProductIds with productId: {}. Reason: {}", productId,
                throwable.getMessage());
        return List.of();
    }

}
