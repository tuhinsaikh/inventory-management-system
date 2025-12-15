package com.retailshop.repository;

import com.retailshop.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
    List<Warehouse> findByIsActiveTrue();
    Boolean existsByWarehouseCode(String warehouseCode);
}
