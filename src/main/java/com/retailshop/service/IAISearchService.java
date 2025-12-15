package com.retailshop.service;

import com.retailshop.dto.response.ProductResponse;
import java.util.List;

public interface IAISearchService {
    List<ProductResponse> intelligentProductSearch(String query);
    String getProductRecommendations(Long productId);
    String naturalLanguageQuery(String question);
}
