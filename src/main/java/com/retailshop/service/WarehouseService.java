package com.retailshop.service;

import com.retailshop.entity.Warehouse;
import com.retailshop.exception.DuplicateResourceException;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService implements IWarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouseRepository.existsByWarehouseCode(warehouse.getWarehouseCode())) {
            throw new DuplicateResourceException("Warehouse code already exists");
        }
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse updateWarehouse(Long warehouseId, Warehouse warehouse) {
        Warehouse existing = getWarehouseById(warehouseId);
        existing.setWarehouseName(warehouse.getWarehouseName());
        existing.setAddress(warehouse.getAddress());
        existing.setCity(warehouse.getCity());
        existing.setState(warehouse.getState());
        existing.setZipCode(warehouse.getZipCode());
        existing.setCountry(warehouse.getCountry());
        existing.setPhone(warehouse.getPhone());
        existing.setIsActive(warehouse.getIsActive());
        return warehouseRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(Long warehouseId) {
        return warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long warehouseId) {
        Warehouse warehouse = getWarehouseById(warehouseId);
        warehouseRepository.delete(warehouse);
    }
}
