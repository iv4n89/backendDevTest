package com.test.backend.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record ProductDetail(
    String id,
    String name,
    BigDecimal price,
    boolean availability
) {
    public ProductDetail {
        // Null checks
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");

        // Business rules
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must not be negative");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private BigDecimal price;
        private boolean availability;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder price(double price) {
            this.price = BigDecimal.valueOf(price);
            return this;
        }

        public Builder availability(boolean availability) {
            this.availability = availability;
            return this;
        }

        public ProductDetail build() {
            return new ProductDetail(id, name, price, availability);
        }
    }
}
