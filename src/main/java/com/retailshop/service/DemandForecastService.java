package com.retailshop.service;

import com.retailshop.entity.InventoryTransaction;
import com.retailshop.entity.Product;
import com.retailshop.repository.InventoryTransactionRepository;
import com.retailshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DemandForecastService implements IDemandForecastService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Override
    public Map<String, Object> forecastDemand(Long productId, Integer days) {
        log.info("Forecasting demand for product: {} over {} days", productId, days);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<InventoryTransaction> transactions = transactionRepository
                .findByProduct_ProductId(productId).stream()
                .filter(t -> t.getTransactionType() == InventoryTransaction.TransactionType.SALE)
                .filter(t -> t.getTransactionDate().isAfter(startDate))
                .collect(Collectors.toList());

        // Simple moving average calculation
        double totalSales = transactions.stream()
                .mapToInt(t -> Math.abs(t.getQuantityChange()))
                .sum();

        double averageDailySales = days > 0 ? totalSales / days : 0;
        double forecastedDemand = averageDailySales * 30; // Next 30 days
        double recommendedReorder = Math.ceil(forecastedDemand * 1.2); // 20% buffer

        Map<String, Object> forecast = new HashMap<>();
        forecast.put("productId", productId);
        forecast.put("productName", product.getProductName());
        forecast.put("historicalPeriodDays", days);
        forecast.put("totalSalesInPeriod", (int) totalSales);
        forecast.put("averageDailySales", Math.round(averageDailySales * 100.0) / 100.0);
        forecast.put("forecastedDemand30Days", Math.round(forecastedDemand * 100.0) / 100.0);
        forecast.put("recommendedReorderQuantity", (int) recommendedReorder);
        forecast.put("currentReorderPoint", product.getReorderPoint());
        forecast.put("forecastMethod", "Simple Moving Average");
        forecast.put("generatedAt", LocalDateTime.now());

        return forecast;
    }

    @Override
    public Map<String, Object> getReorderRecommendations() {
        log.info("Generating reorder recommendations");

        List<Product> products = productRepository.findAll();
        List<Map<String, Object>> recommendations = new ArrayList<>();

        for (Product product : products) {
            try {
                Map<String, Object> forecast = forecastDemand(product.getProductId(), 30);
                double avgDailySales = (Double) forecast.get("averageDailySales");

                // Add to recommendations if there's significant demand
                if (avgDailySales > 0) {
                    recommendations.add(forecast);
                }
            } catch (Exception e) {
                log.error("Error forecasting for product: {}", product.getProductId(), e);
            }
        }

        // Sort by forecasted demand (highest first)
        recommendations.sort((a, b) -> {
            Double demandA = (Double) a.get("forecastedDemand30Days");
            Double demandB = (Double) b.get("forecastedDemand30Days");
            return demandB.compareTo(demandA);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("recommendations", recommendations);
        result.put("totalProducts", recommendations.size());
        result.put("generatedAt", LocalDateTime.now());
        result.put("forecastMethod", "Simple Moving Average (30-day history)");

        return result;
    }

    @Override
    public Double predictNextMonthSales(Long productId) {
        log.info("Predicting next month sales for product: {}", productId);

        Map<String, Object> forecast = forecastDemand(productId, 60);
        return (Double) forecast.get("forecastedDemand30Days");
    }
}
