package com.test.backend.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import com.test.backend.domain.exception.ExternalServiceException;
import com.test.backend.domain.model.ProductDetail;
import com.test.backend.infrastructure.config.TestBeansConfig;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestClientTest(ProductApiClient.class)
@Import({ TestBeansConfig.class })
@DisplayName("Product API Client Test")
public class ProductApiClientTest {

    @Autowired
    private ProductApiClient productApiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    private static final String BASE_URL = "http://localhost:3001";
    private static final String PRODUCT_ID = "1";

    @BeforeEach
    void setup() {
        mockServer.reset();
    }

    @Test
    @DisplayName("Should return product when API responds with 200")
    void shouldReturnProductWhenApiRespondsOk() {
        // Given
        String expectedJson = """
                {
                    "id": "1",
                    "name": "T-Shirt",
                    "price": 15.99,
                    "availability": true
                }
                """;

        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        // When
        Optional<ProductDetail> result = productApiClient.getProductById(PRODUCT_ID);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("1");
        assertThat(result.get().name()).isEqualTo("T-Shirt");
        assertThat(result.get().price()).isEqualTo(BigDecimal.valueOf(15.99));
        assertThat(result.get().availability()).isTrue();

        mockServer.verify();
    }

    @Test
    @DisplayName("Should return empty when API responds with 404")
    void shouldResturnEmptyWhenApiRespondsNotFound() {
        // Given
        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // When
        Optional<ProductDetail> result = productApiClient.getProductById(PRODUCT_ID);

        // Then
        assertThat(result).isEmpty();

        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw ExternalServiceException when API responds with 500")
    void shouldThrowExceptionWhenApiResponds500() {
        // Given
        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // When & Then
        assertThatThrownBy(() -> productApiClient.getProductById(PRODUCT_ID))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Failed to fetch product: " + PRODUCT_ID);

        mockServer.verify();
    }
}
