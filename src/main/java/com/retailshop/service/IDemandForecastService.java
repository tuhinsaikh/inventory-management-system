package com.retailshop.service;

import java.util.Map;

public interface IDemandForecastService {
    Map<String, Object> forecastDemand(Long productId, Integer days);
    Map<String, Object> getReorderRecommendations();
    Double predictNextMonthSales(Long productId);
}
