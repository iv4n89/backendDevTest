package com.test.backend.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.domain.port.output.SimilarIdsPort;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestBeansConfig {

    @Bean
    @Primary
    public GetSimilarProductsUseCase mockGetSimilarProductsUseCase() {
        return mock(GetSimilarProductsUseCase.class);
    }

    @Bean
    @Primary
    public ProductPort mockProductPort() {
        return mock(ProductPort.class);
    }

    @Bean
    @Primary
    public SimilarIdsPort mockSimilarIdsPort() {
        return mock(SimilarIdsPort.class);
    }
}
