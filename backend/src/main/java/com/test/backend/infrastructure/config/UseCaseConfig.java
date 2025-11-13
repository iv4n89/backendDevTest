package com.test.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.test.backend.application.usecases.GetSimilarProductsUseCaseImpl;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.domain.port.output.SimilarIdsPort;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetSimilarProductsUseCase getSimilarProductsUseCase(SimilarIdsPort similarIdsPort, ProductPort productPort) {
        return new GetSimilarProductsUseCaseImpl(
                similarIdsPort,
                productPort);
    }
}
