package com.retailshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @Column(unique = true, nullable = false, length = 20)
    private String supplierCode;

    @Column(nullable = false, length = 100)
    private String supplierName;

    @Column(length = 100)
    private String contactPerson;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 20)
    private String zipCode;

    @Column(length = 50)
    private String country;

    @Column(length = 50)
    private String paymentTerms;

    private Integer leadTimeDays;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
