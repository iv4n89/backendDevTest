package com.test.backend.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.test.backend.domain.model.ProductDetail;
import com.test.backend.domain.mother.ProductMother;
import com.test.backend.infrastructure.dto.ProductResponse;

@DisplayName("Product REST Mapper Test")
public class ProductRestMapperTest {

    @Test
    @DisplayName("Shuld map ProductDetail to ProductResponse")
    void shouldMapProductDetailToProductResponse() {
        // Given
        ProductDetail productDetail = ProductMother.random();

        // When
        ProductResponse response = ProductRestMapper.toProductResponse(productDetail);

        // Then
        assertThat(response.id()).isEqualTo(productDetail.id());
        assertThat(response.name()).isEqualTo(productDetail.name());
        assertThat(response.price()).isEqualTo(productDetail.price());
        assertThat(response.availability()).isEqualTo(productDetail.availability());
    }

    @Test
    @DisplayName("Should map ProductResponse to ProductDetail")
    void shouldMapProductResponseToProductDetail() {
        // Given
        ProductResponse response = new ProductResponse("1", "Dress", BigDecimal.valueOf(19.99), true);

        // When
        ProductDetail productDetail = ProductRestMapper.toDomainProductDetail(response);

        // Then
        assertThat(productDetail.id()).isEqualTo(response.id());
        assertThat(productDetail.name()).isEqualTo(response.name());
        assertThat(productDetail.price()).isEqualTo(response.price());
        assertThat(productDetail.availability()).isEqualTo(response.availability());
    }

    @Test
    @DisplayName("Should return a list of ProductDetail from a list of ProductResponse")
    void shouldReturnListOfProductDetailFromListOfProductResponse() {
        // Given
        ProductResponse response1 = new ProductResponse("1", "Dress", BigDecimal.valueOf(19.99), true);
        ProductResponse response2 = new ProductResponse("2", "Shirt", BigDecimal.valueOf(29.99), false);
        var responses = List.of(response1, response2);

        // When
        var productDetails = ProductRestMapper.toDomainProductDetailList(responses);

        // Then
        assertThat(productDetails).hasSize(2);

        assertThat(productDetails.get(0).id()).isEqualTo(response1.id());
        assertThat(productDetails.get(0).name()).isEqualTo(response1.name());
        assertThat(productDetails.get(0).price()).isEqualTo(response1.price());
        assertThat(productDetails.get(0).availability()).isEqualTo(response1.availability());

        assertThat(productDetails.get(1).id()).isEqualTo(response2.id());
        assertThat(productDetails.get(1).name()).isEqualTo(response2.name());
        assertThat(productDetails.get(1).price()).isEqualTo(response2.price());
        assertThat(productDetails.get(1).availability()).isEqualTo(response2.availability());
    }

    @Test
    @DisplayName("Should return a list of ProductResponse from a list of ProductDetail")
    void shouldReturnListOfProductResponseFromListOfProductDetail() {
        // Given
        ProductDetail product1 = ProductMother.random();
        ProductDetail product2 = ProductMother.random();
        var products = List.of(product1, product2);

        // When
        var responses = ProductRestMapper.toProductResponseList(products);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).id()).isEqualTo(product1.id());
        assertThat(responses.get(0).name()).isEqualTo(product1.name());
        assertThat(responses.get(0).price()).isEqualTo(product1.price());
        assertThat(responses.get(0).availability()).isEqualTo(product1.availability());
        assertThat(responses.get(1).id()).isEqualTo(product2.id());
        assertThat(responses.get(1).name()).isEqualTo(product2.name());
        assertThat(responses.get(1).price()).isEqualTo(product2.price());
        assertThat(responses.get(1).availability()).isEqualTo(product2.availability());
    }
}
