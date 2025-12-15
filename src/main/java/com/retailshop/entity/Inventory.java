package com.retailshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}),
        indexes = {
                @Index(name = "idx_product", columnList = "product_id"),
                @Index(name = "idx_warehouse", columnList = "warehouse_id")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantityOnHand = 0;

    @Column(nullable = false)
    private Integer quantityReserved = 0;

    private LocalDate lastRestockDate;

    private LocalDate lastCountDate;

    @Column(length = 50)
    private String binLocation;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Transient
    public Integer getQuantityAvailable() {
        return quantityOnHand - quantityReserved;
    }
}

