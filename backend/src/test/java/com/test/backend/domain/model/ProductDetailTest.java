package com.test.backend.domain.model;

import org.junit.jupiter.api.Test;

import com.test.backend.domain.mother.ProductMother;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

public class ProductDetailTest {
    @Test
    void shouldCreateRandomProductDetail() {
        // When
        ProductDetail productDetail = ProductMother.random();

        // Then
        assertThat(productDetail.id()).isNotNull();
        assertThat(productDetail.name()).isNotNull();
        assertThat(productDetail.price()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void shouldCreateProductDetailWithGivenId() {
        // Given
        String expectedId = "12345";

        // When
        ProductDetail productDetail = ProductMother.withId(expectedId);

        // Then
        assertThat(productDetail.id()).isEqualTo(expectedId);
    }

    @Test
    void shouldCreateProductDetailWithGivenName() {
        // Given
        String expectedName = "Test Product";

        // When
        ProductDetail productDetail = ProductMother.withName(expectedName);

        // Then
        assertThat(productDetail.name()).isEqualTo(expectedName);
    }

    @Test
    void shouldCreateProductDetailWithGivenPrice() {
        // Given
        double expectedPrice = 99.99;

        // When
        ProductDetail productDetail = ProductMother.withPrice(expectedPrice);

        // Then
        assertThat(productDetail.price()).isEqualByComparingTo(BigDecimal.valueOf(expectedPrice));
    }

    @Test
    void shouldCreateProductDetailWithGivenAvailability() {
        // Given
        boolean expectedAvailability = true;

        // When
        ProductDetail productDetail = ProductMother.withAvailability(expectedAvailability);

        // Then
        assertThat(productDetail.availability()).isEqualTo(expectedAvailability);
    }
}
