package com.test.backend.domain.port.output;

import java.util.List;

public interface SimilarIdsPort {
    List<String> getSimilarProductIds(String productId);
}
