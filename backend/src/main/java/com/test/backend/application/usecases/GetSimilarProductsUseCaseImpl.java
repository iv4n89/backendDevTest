package com.test.backend.application.usecases;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.domain.port.output.SimilarIdsPort;

public class GetSimilarProductsUseCaseImpl implements GetSimilarProductsUseCase {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SimilarIdsPort similarIdsPort;
    private final ProductPort productPort;

    public GetSimilarProductsUseCaseImpl(SimilarIdsPort similarIdsPort, ProductPort productPort) {
        this.similarIdsPort = similarIdsPort;
        this.productPort = productPort;
    }

    @Override
    public List<ProductDetail> execute(String productId) {
        log.info("Getting similar products for: {}", productId);

        List<String> similarIds = getSimilarId(productId);

        if (similarIds.isEmpty()) {
            log.info("No similar products found for: {}", productId);
            return List.of();
        }

        List<ProductDetail> products = getProductsInParallel(similarIds);

        log.info("Found {} similar products for: {}", products.size(), productId);
        return products;
    }

    private List<String> getSimilarId(String productId) {
        try {
            return similarIdsPort.getSimilarProductIds(productId);
        } catch (Exception e) {
            log.error("Failed to get similar IDs: {}", e.getMessage());
            return List.of();
        }
    }

    private List<ProductDetail> getProductsInParallel(List<String> similarIds) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ProductDetail>> futures = similarIds.stream()
                    .map(id -> executor.submit(() -> fetchProduct(id)))
                    .toList();

            return futures.stream()
                    .map(this::getResult)
                    .filter(product -> product != null)
                    .filter(ProductDetail::availability)
                    .toList();
        }
    }

    private ProductDetail fetchProduct(String productId) {
        try {
            return productPort.getProductById(productId).orElse(null);
        } catch (Exception e) {
            log.warn("Could not fetch product {}: {}", productId, e.getMessage());
            return null;
        }
    }

    private ProductDetail getResult(Future<ProductDetail> future) {
        try {
            return future.get();
        } catch (Exception e) {
            log.warn("Error retrieving product detail from future: {}", e.getMessage());
            return null;
        }
    }
}