package com.test.backend.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.mother.ProductMother;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.domain.port.output.ProductPort;
import com.test.backend.domain.port.output.SimilarIdsPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetSimilarProductsUseCaseImpl Test")
public class GetSimilarProductsUseCaseImplTest {
    @Mock
    private SimilarIdsPort similarIdsPort;

    @Mock
    private ProductPort productPort;

    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @BeforeEach
    void setUp() {
        getSimilarProductsUseCase = new GetSimilarProductsUseCaseImpl(similarIdsPort, productPort);
    }

    @Test
    @DisplayName("Should retrieve similar products successfully")
    void shouldReturnSimilarProducts() {
        // Given
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");

        when(similarIdsPort.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productPort.getProductById("2")).thenReturn(Optional.of(ProductMother.withId("2", true)));
        when(productPort.getProductById("3")).thenReturn(Optional.of(ProductMother.withId("3", true)));
        when(productPort.getProductById("4")).thenReturn(Optional.of(ProductMother.withId("4", true)));

        // When
        List<ProductDetail> result = getSimilarProductsUseCase.execute(productId);

        // Then
        assertThat(result)
                .hasSize(3)
                .extracting(ProductDetail::id)
                .containsExactly("2", "3", "4");
    }

    @Test
    @DisplayName("Should filter out products that could not be retrieved")
    void shouldFilterOutFailedProducts() {
        // Given
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");

        when(similarIdsPort.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productPort.getProductById("2")).thenReturn(Optional.of(ProductMother.withId("2", true)));
        when(productPort.getProductById("3")).thenReturn(Optional.empty());
        when(productPort.getProductById("4")).thenReturn(Optional.of(ProductMother.withId("4", true)));

        // When
        List<ProductDetail> result = getSimilarProductsUseCase.execute(productId);

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(ProductDetail::id)
                .containsExactly("2", "4");
    }

    @Test
    @DisplayName("Should return empty list when no similar products found")
    void shouldReturnEmptyWhenNoSimilarProducts() {
        // Given
        String productId = "1";
        when(similarIdsPort.getSimilarProductIds(productId)).thenReturn(List.of());

        // When
        List<ProductDetail> result = getSimilarProductsUseCase.execute(productId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when all product retrievals fail")
    void shouldReturnEmptyWhenAllProductsFail() {
        // Given
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");

        when(similarIdsPort.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productPort.getProductById("2")).thenReturn(Optional.empty());
        when(productPort.getProductById("3")).thenReturn(Optional.empty());
        when(productPort.getProductById("4")).thenReturn(Optional.empty());

        // When
        List<ProductDetail> result = getSimilarProductsUseCase.execute(productId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle large number of similar products")
    void shouldHandleLargeNumberOfSimilarProducts() {
        // Given
        String productId = "1";
        int count = 100;
        List<String> similarIds = IntStream.rangeClosed(2, count + 1)
                .mapToObj(String::valueOf)
                .toList();

        when(similarIdsPort.getSimilarProductIds(productId)).thenReturn(similarIds);
        similarIds.forEach(id -> when(productPort.getProductById(id))
                .thenReturn(Optional.of(ProductMother.withId(id, true))));

        // When
        List<ProductDetail> result = getSimilarProductsUseCase.execute(productId);

        // Then
        assertThat(result)
                .hasSize(count)
                .extracting(ProductDetail::id)
                .containsExactlyElementsOf(similarIds);
    }
}
