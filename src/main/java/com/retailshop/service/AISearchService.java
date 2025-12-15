package com.retailshop.service;

import com.retailshop.dto.response.ProductResponse;
import com.retailshop.entity.Product;
import com.retailshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AISearchService implements IAISearchService {

    private final ProductRepository productRepository;
    private final IProductService productService;

    @Override
    public List<ProductResponse> intelligentProductSearch(String query) {
        log.info("Performing intelligent search for query: {}", query);

        // Enhanced search logic without AI
        String[] keywords = query.toLowerCase().split("\\s+");

        List<Product> products = productRepository.searchProducts(query);

        return products.stream()
                .map(p -> productService.getProductById(p.getProductId()))
                .collect(Collectors.toList());
    }

    @Override
    public String getProductRecommendations(Long productId) {
        log.info("Getting recommendations for product: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Simple recommendation logic
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("Recommended products based on: ").append(product.getProductName()).append("\n\n");

        // Find products in the same category
        if (product.getCategory() != null) {
            List<Product> relatedProducts = productRepository
                    .findByCategory_CategoryId(product.getCategory().getCategoryId())
                    .stream()
                    .filter(p -> !p.getProductId().equals(productId))
                    .limit(5)
                    .collect(Collectors.toList());

            if (!relatedProducts.isEmpty()) {
                recommendations.append("Related products in ").append(product.getCategory().getCategoryName()).append(":\n");
                for (Product p : relatedProducts) {
                    recommendations.append("- ").append(p.getProductName())
                            .append(" (SKU: ").append(p.getSku())
                            .append(", Price: $").append(p.getSellingPrice()).append(")\n");
                }
            } else {
                recommendations.append("No related products found in this category.\n");
            }
        } else {
            recommendations.append("This product has no category assigned. Unable to provide recommendations.\n");
        }

        return recommendations.toString();
    }

    @Override
    public String naturalLanguageQuery(String question) {
        log.info("Processing natural language query: {}", question);

        String lowerQuestion = question.toLowerCase();

        // Simple keyword-based responses
        if (lowerQuestion.contains("how many") || lowerQuestion.contains("count")) {
            long productCount = productRepository.count();
            return String.format("Total number of products in inventory: %d", productCount);
        }

        if (lowerQuestion.contains("low stock") || lowerQuestion.contains("out of stock")) {
            return "To check low stock items, please use the inventory low-stock endpoint: GET /api/inventory/low-stock";
        }

        if (lowerQuestion.contains("categories")) {
            return "To view all categories, please use: GET /api/categories";
        }

        if (lowerQuestion.contains("search") || lowerQuestion.contains("find")) {
            return "To search for products, use: GET /api/products/search?keyword=your_keyword";
        }

        // Default response
        return String.format(
                "I understand you're asking: '%s'\n\n" +
                        "Here are some available operations:\n" +
                        "- View all products: GET /api/products\n" +
                        "- Search products: GET /api/products/search?keyword=term\n" +
                        "- Check inventory: GET /api/inventory\n" +
                        "- View low stock: GET /api/inventory/low-stock\n" +
                        "- View categories: GET /api/categories\n\n" +
                        "Note: Advanced AI features require OpenAI API configuration.",
                question
        );
    }
}
