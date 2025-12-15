package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.dto.response.ProductResponse;
import com.retailshop.service.IAISearchService;
import com.retailshop.service.IDemandForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final IAISearchService aiSearchService;
    private final IDemandForecastService demandForecastService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> intelligentSearch(@RequestParam String query) {
        List<ProductResponse> products = aiSearchService.intelligentProductSearch(query);
        ApiResponse<List<ProductResponse>> response = ApiResponse.<List<ProductResponse>>builder()
                .success(true)
                .message("AI search completed successfully")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations/{productId}")
    public ResponseEntity<ApiResponse<String>> getRecommendations(@PathVariable Long productId) {
        String recommendations = aiSearchService.getProductRecommendations(productId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Recommendations generated successfully")
                .data(recommendations)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/query")
    public ResponseEntity<ApiResponse<String>> naturalLanguageQuery(@RequestParam String question) {
        String answer = aiSearchService.naturalLanguageQuery(question);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Query processed successfully")
                .data(answer)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/forecast/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> forecastDemand(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "30") Integer days) {
        Map<String, Object> forecast = demandForecastService.forecastDemand(productId, days);
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Demand forecast generated successfully")
                .data(forecast)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reorder-recommendations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReorderRecommendations() {
        Map<String, Object> recommendations = demandForecastService.getReorderRecommendations();
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Reorder recommendations generated successfully")
                .data(recommendations)
                .build();
        return ResponseEntity.ok(response);
    }
}
