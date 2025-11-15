package com.test.backend.infrastructure.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductResponse(
        @JsonProperty("id") String id,

        @JsonProperty("name") String name,

        @JsonProperty("price") BigDecimal price,

        @JsonProperty("availability") boolean availability) {
}
