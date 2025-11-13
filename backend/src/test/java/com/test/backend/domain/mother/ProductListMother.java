package com.test.backend.domain.mother;

import java.util.List;
import java.util.stream.IntStream;

import com.test.backend.domain.model.ProductDetail;

public class ProductListMother {
    private ProductListMother() {
    }

    public static List<ProductDetail> random(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> ProductMother.random())
                .toList();
    }

    public static List<ProductDetail> withIds(List<String> ids) {
        return ids.stream()
                .map(ProductMother::withId)
                .toList();
    }

    public static List<ProductDetail> withNames(List<String> names) {
        return names.stream()
                .map(ProductMother::withName)
                .toList();
    }

    public static List<ProductDetail> withPrices(List<Double> prices) {
        return prices.stream()
                .map(ProductMother::withPrice)
                .toList();
    }

    public static List<ProductDetail> withAvailabilities(List<Boolean> availabilities) {
        return availabilities.stream()
                .map(ProductMother::withAvailability)
                .toList();
    }
}
