package com.test.backend.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

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
import com.test.backend.infrastructure.config.TestBeansConfig;

@RestClientTest(SimilarIdsApiClient.class)
@Import({ TestBeansConfig.class })
@DisplayName("Similar IDs API Client Test")
public class SimilarIdsApiClientTest {

    @Autowired
    private SimilarIdsApiClient similarIdsApiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    private static final String BASE_URL = "http://localhost:3001";
    private static final String PRODUCT_ID = "1";

    @BeforeEach
    void setUp() {
        mockServer.reset();
    }

    @Test
    @DisplayName("Should return similar IDs when API responds with 200")
    void shouldReturnSimilarIdsWhenApiRespondsOk() {
        // Given
        String expectedJson = """
                ["2", "3", "4", "5"]
                """;

        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID + "/similarids"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        // When
        List<String> result = similarIdsApiClient.getSimilarProductIds(PRODUCT_ID);

        // Then
        assertThat(result)
                .hasSize(4)
                .containsExactly("2", "3", "4", "5");

        mockServer.verify();
    }

    @Test
    @DisplayName("Should return empty list when API responds with 404")
    void shouldReturnEmptyListWhenApiResponds404() {
        // Given
        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID + "/similarids"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // When
        List<String> result = similarIdsApiClient.getSimilarProductIds(PRODUCT_ID);

        // Then
        assertThat(result).isEmpty();
        mockServer.verify();
    }

    @Test
    @DisplayName("Should throw ExternalServiceException when API responds with 500")
    void shouldThrowExceptionWhenApiResponds500() {
        // Given
        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID + "/similarids"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // When & Then
        assertThatThrownBy(() -> similarIdsApiClient.getSimilarProductIds(PRODUCT_ID))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage("Failed to fetch similar IDs for product: " + PRODUCT_ID);

        mockServer.verify();
    }

    @Test
    @DisplayName("Should return empty list when response is empty array")
    void shouldReturnEmptyListWhenResponseIsEmptyArray() {
        // Given
        String expectedJson = "[]";

        mockServer.expect(requestTo(BASE_URL + "/product/" + PRODUCT_ID + "/similarids"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        // When
        List<String> result = similarIdsApiClient.getSimilarProductIds(PRODUCT_ID);

        // Then
        assertThat(result).isEmpty();

        mockServer.verify();
    }
}
