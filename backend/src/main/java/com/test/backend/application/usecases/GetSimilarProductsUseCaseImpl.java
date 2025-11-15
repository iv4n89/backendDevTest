package com.test.backend.application.usecases;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.domain.port.output.SimilarIdsPort;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        return getSimilarProductsReactive(productId)
                .collectList()
                .block();
    }

    private Flux<ProductDetail> getSimilarProductsReactive(String productId) {
        List<String> similarIds = similarIdsPort.getSimilarProductIds(productId);

        if (similarIds.isEmpty()) {
            log.info("No similar products found for: {}", productId);
            return Flux.empty();
        }

        return Flux.fromIterable(similarIds)
                .flatMap(id -> Mono.fromCallable(() -> productPort.getProductById(id))
                        .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                        .onErrorResume(ex -> {
                            log.warn("Error fetching product {}: {}", id, ex.getMessage());
                            return Mono.just(Optional.empty());
                        }))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ProductDetail::availability);
    }
}