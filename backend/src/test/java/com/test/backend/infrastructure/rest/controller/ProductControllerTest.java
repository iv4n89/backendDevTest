package com.test.backend.infrastructure.rest.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // ⭐ ESTE
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.port.input.GetSimilarProductsUseCase;
import com.test.backend.infrastructure.exception.GlobalExceptionHandler;

@WebMvcTest(controllers = ProductController.class)
@Import({ GlobalExceptionHandler.class })
@DisplayName("Product Controller Test")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public GetSimilarProductsUseCase getSimilarProductsUseCase() {
            return mock(GetSimilarProductsUseCase.class);
        }
    }

    @BeforeEach
    void setup() {
        reset(getSimilarProductsUseCase);
    }

    @Test
    @DisplayName("Should return 200 with similar products")
    void shouldReturnSimilarProducts() throws Exception {
        // Given
        String productId = "1";
        List<ProductDetail> products = List.of(
                ProductDetail.builder().id("2").name("Dress").price(BigDecimal.valueOf(19.99)).availability(true)
                        .build(),
                ProductDetail.builder().id("3").name("Shirt").price(BigDecimal.valueOf(29.99)).availability(true)
                        .build());

        when(getSimilarProductsUseCase.execute(productId)).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("2")))
                .andExpect(jsonPath("$[0].name", is("Dress")))
                .andExpect(jsonPath("$[0].price", is(19.99)))
                .andExpect(jsonPath("$[0].availability", is(true)))
                .andExpect(jsonPath("$[1].id", is("3")))
                .andExpect(jsonPath("$[1].name", is("Shirt")));

        // Verify
        verify(getSimilarProductsUseCase, times(1)).execute(productId);
    }

    @Test
    @DisplayName("Should return 200 with empty list when no similar products found")
    void shouldReturnEmptyArrayWhenNoSimilars() throws Exception {
        // Given
        String productId = "1";

        when(getSimilarProductsUseCase.execute(productId))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        // Verify
        verify(getSimilarProductsUseCase, times(1)).execute(productId);
    }

    @Test
    @DisplayName("Should return 500 when external service fails")
    void shouldReturn500WhenExternalServiceFails() throws Exception {
        // Given
        String productId = "999";

        when(getSimilarProductsUseCase.execute(productId))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("Internal Server Error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message").value(containsString("Service unavailable")));

        // Verify
        verify(getSimilarProductsUseCase).execute(productId);
    }

    @Test
    @DisplayName("Should return 500 for unexpected errors")
    void shouldReturn500ForUnexpectedErrors() throws Exception {
        // Given
        String productId = "1";
        when(getSimilarProductsUseCase.execute(productId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(get("/product/{productId}/similar", productId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("Internal Server Error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.message", is("Unexpected error")));

        // Verify
        verify(getSimilarProductsUseCase).execute(productId);
    }
}
