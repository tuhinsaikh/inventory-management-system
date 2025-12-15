package com.retailshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_suppliers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "supplier_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ProductSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productSupplierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(length = 50)
    private String supplierSku;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitCost;

    private Integer leadTimeDays;

    private Integer minOrderQuantity;

    @Column(nullable = false)
    private Boolean isPreferred = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
