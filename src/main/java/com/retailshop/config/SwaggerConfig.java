package com.retailshop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "RetailShop Inventory Management API",
                description = "Complete Inventory Management System API with Purchase/Sales Orders",
                version = "1.0.0",
                contact = @Contact(
                        name = "RetailShop Team",
                        email = "support@retailshop.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://github.com/retailshop/inventory-management"
                )
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerConfig {
}
