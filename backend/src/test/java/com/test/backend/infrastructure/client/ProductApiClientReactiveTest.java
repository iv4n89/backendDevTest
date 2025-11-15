package com.test.backend.infrastructure.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.backend.domain.model.ProductDetail;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;

@SpringBootTest
@DisplayName("Product API Client Test")
public class ProductApiClientReactiveTest {

    private ProductApiClientReactive productApiClient;
    private MockWebServer mockWebServer;
    private String baseUrl;

    @BeforeEach
    void setup() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = mockWebServer.url("/").toString().replaceAll("/$", "");

        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        productApiClient = new ProductApiClientReactive(webClient);
        
        var field = ProductApiClientReactive.class.getDeclaredField("baseUrl");
        field.setAccessible(true);
        field.set(productApiClient, baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should return product when API responds with 200")
    void shouldReturnProductWhenApiRespondsOk() {
        // Given
        String productId = "1";
        String expectedJson = """
                {
                    "id": "1",
                    "name": "T-Shirt",
                    "price": 15.99,
                    "availability": true
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(expectedJson)
                .addHeader("Content-Type", "application/json"));

        // When
        Optional<ProductDetail> result = productApiClient.getProductById(productId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("1");
        assertThat(result.get().name()).isEqualTo("T-Shirt");
        assertThat(result.get().price()).isEqualTo(BigDecimal.valueOf(15.99));
        assertThat(result.get().availability()).isTrue();
    }

    @Test
    @DisplayName("Should return empty when API responds with 404")
    void shouldReturnEmptyWhenApiRespondsNotFound() {
        // Given
        String productId = "999";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        // When
        Optional<ProductDetail> result = productApiClient.getProductById(productId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when API responds with 500")
    void shouldReturnEmptyWhenApiResponds500() {
        // Given
        String productId = "1";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json"));

        // When
        Optional<ProductDetail> result = productApiClient.getProductById(productId);

        // Then
        assertThat(result).isEmpty();
    }
}
