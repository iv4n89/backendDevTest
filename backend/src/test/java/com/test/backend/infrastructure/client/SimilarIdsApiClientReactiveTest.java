package com.test.backend.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.test.backend.domain.exception.ExternalApiException;
import com.test.backend.domain.exception.ProductNotFoundException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@DisplayName("Similar IDs API Client Test")
public class SimilarIdsApiClientReactiveTest {

    private SimilarIdsApiClientReactive similarIdsApiClient;
    private MockWebServer mockWebServer;
    private String baseUrl;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        baseUrl = mockWebServer.url("/").toString().replaceAll("/$", "");

        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        similarIdsApiClient = new SimilarIdsApiClientReactive(webClient);

        // Set the baseUrl via reflection since it's a @Value field
        var field = SimilarIdsApiClientReactive.class.getDeclaredField("baseUrl");
        field.setAccessible(true);
        field.set(similarIdsApiClient, baseUrl);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Should return similar IDs when API responds with 200")
    void shouldReturnSimilarIdsWhenApiRespondsOk() {
        // Given
        String productId = "1";
        String expectedJson = """
                ["2", "3", "4", "5"]
                """;

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(expectedJson)
                .addHeader("Content-Type", "application/json"));

        // When
        List<String> result = similarIdsApiClient.getSimilarProductIds(productId);

        // Then
        assertThat(result)
                .hasSize(4)
                .containsExactly("2", "3", "4", "5");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when API responds with 404")
    void shouldThrowProductNotFoundExceptionWhenApiResponds404() {
        // Given
        String productId = "999";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> {
            similarIdsApiClient.getSimilarProductIds(productId);
        });
    }

    @Test
    @DisplayName("Should throw ExternalApiException when API responds with 500")
    void shouldThrowExternalApiExceptionWhenApiResponds500() {
        // Given
        String productId = "1";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", "application/json"));

        // When & Then
        assertThrows(ExternalApiException.class, () -> {
            similarIdsApiClient.getSimilarProductIds(productId);
        });
    }

    @Test
    @DisplayName("Should return empty list when response is empty array")
    void shouldReturnEmptyListWhenResponseIsEmptyArray() {
        // Given
        String productId = "1";
        String expectedJson = "[]";

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(expectedJson)
                .addHeader("Content-Type", "application/json"));

        // When
        List<String> result = similarIdsApiClient.getSimilarProductIds(productId);

        // Then
        assertThat(result).isEmpty();
    }
}

