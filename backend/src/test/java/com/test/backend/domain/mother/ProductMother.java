package com.test.backend.domain.mother;

import java.util.Locale;

import com.test.backend.domain.model.ProductDetail;

import net.datafaker.Faker;

public class ProductMother {
    private static final Faker faker = new Faker(Locale.forLanguageTag("es"));

    private ProductMother() {
    }

    public static ProductDetail random() {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(faker.commerce().productName())
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(faker.bool().bool())
                .build();
    }

    public static ProductDetail withId(String id) {
        return ProductDetail.builder()
                .id(id)
                .name(faker.commerce().productName())
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(faker.bool().bool())
                .build();
    }

    public static ProductDetail withId(String id, boolean availability) {
        return ProductDetail.builder()
                .id(id)
                .name(faker.commerce().productName())
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(availability)
                .build();
    }

    public static ProductDetail withName(String name) {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(name)
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(faker.bool().bool())
                .build();
    }

    public static ProductDetail withName(String name, boolean availability) {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(name)
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(availability)
                .build();
    }

    public static ProductDetail withPrice(double price) {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(faker.commerce().productName())
                .price(price)
                .availability(faker.bool().bool())
                .build();
    }

    public static ProductDetail withPrice(double price, boolean availability) {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(faker.commerce().productName())
                .price(price)
                .availability(availability)
                .build();
    }

    public static ProductDetail withAvailability(boolean availability) {
        return ProductDetail.builder()
                .id(faker.number().digit().toString())
                .name(faker.commerce().productName())
                .price(faker.number().randomDouble(2, 10, 1000))
                .availability(availability)
                .build();
    }
}
