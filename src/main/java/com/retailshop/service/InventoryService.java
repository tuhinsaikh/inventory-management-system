package com.retailshop.service;

import com.retailshop.dto.request.InventoryUpdateRequest;
import com.retailshop.dto.response.InventoryResponse;
import com.retailshop.entity.Inventory;
import com.retailshop.entity.InventoryTransaction;
import com.retailshop.entity.Product;
import com.retailshop.entity.Warehouse;
import com.retailshop.exception.InsufficientStockException;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.InventoryRepository;
import com.retailshop.repository.InventoryTransactionRepository;
import com.retailshop.repository.ProductRepository;
import com.retailshop.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryTransactionRepository transactionRepository;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId, Long warehouseId) {
        Inventory inventory = inventoryRepository.findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product and warehouse"));
        return convertToResponse(inventory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByProduct(Long productId) {
        return inventoryRepository.findByProduct_ProductId(productId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventoryByWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouse_WarehouseId(warehouseId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(InventoryUpdateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Inventory inventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(request.getProductId(), request.getWarehouseId())
                .orElse(Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantityOnHand(0)
                        .quantityReserved(0)
                        .build());

        Integer oldQuantity = inventory.getQuantityOnHand();
        inventory.setQuantityOnHand(request.getQuantityOnHand());
        inventory.setLastRestockDate(LocalDate.now());

        if (request.getBinLocation() != null) {
            inventory.setBinLocation(request.getBinLocation());
        }

        inventory = inventoryRepository.save(inventory);

        // Record transaction
        createTransaction(product, warehouse, InventoryTransaction.TransactionType.ADJUSTMENT,
                request.getQuantityOnHand() - oldQuantity, oldQuantity, inventory.getQuantityOnHand(),
                "Manual inventory update");

        return convertToResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse adjustInventory(Long productId, Long warehouseId, Integer quantity, String reason) {
        Inventory inventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        Integer oldQuantity = inventory.getQuantityOnHand();
        Integer newQuantity = oldQuantity + quantity;

        if (newQuantity < 0) {
            throw new InsufficientStockException("Adjustment would result in negative stock");
        }

        inventory.setQuantityOnHand(newQuantity);
        inventory = inventoryRepository.save(inventory);

        // Record transaction
        createTransaction(inventory.getProduct(), inventory.getWarehouse(),
                InventoryTransaction.TransactionType.ADJUSTMENT,
                quantity, oldQuantity, newQuantity, reason);

        return convertToResponse(inventory);
    }

    @Override
    @Transactional
    public void transferStock(Long productId, Long fromWarehouseId, Long toWarehouseId, Integer quantity) {
        // Deduct from source warehouse
        Inventory fromInventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, fromWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Source inventory not found"));

        if (fromInventory.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException("Insufficient stock for transfer");
        }

        Integer fromOldQty = fromInventory.getQuantityOnHand();
        fromInventory.setQuantityOnHand(fromInventory.getQuantityOnHand() - quantity);
        inventoryRepository.save(fromInventory);

        // Add to destination warehouse
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination warehouse not found"));

        Inventory toInventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, toWarehouseId)
                .orElse(Inventory.builder()
                        .product(product)
                        .warehouse(toWarehouse)
                        .quantityOnHand(0)
                        .quantityReserved(0)
                        .build());

        Integer toOldQty = toInventory.getQuantityOnHand();
        toInventory.setQuantityOnHand(toInventory.getQuantityOnHand() + quantity);
        inventoryRepository.save(toInventory);

        // Record transactions
        createTransaction(product, fromInventory.getWarehouse(), InventoryTransaction.TransactionType.TRANSFER,
                -quantity, fromOldQty, fromInventory.getQuantityOnHand(),
                "Transfer to warehouse: " + toWarehouse.getWarehouseName());

        createTransaction(product, toWarehouse, InventoryTransaction.TransactionType.TRANSFER,
                quantity, toOldQty, toInventory.getQuantityOnHand(),
                "Transfer from warehouse: " + fromInventory.getWarehouse().getWarehouseName());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalStockByProduct(Long productId) {
        Integer total = inventoryRepository.getTotalStockByProduct(productId);
        return total != null ? total : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkStockAvailability(Long productId, Long warehouseId, Integer requiredQuantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId)
                .orElse(null);

        return inventory != null && inventory.getQuantityAvailable() >= requiredQuantity;
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, Long warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        if (inventory.getQuantityAvailable() < quantity) {
            throw new InsufficientStockException("Insufficient available stock for reservation");
        }

        inventory.setQuantityReserved(inventory.getQuantityReserved() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void releaseReservedStock(Long productId, Long warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository
                .findByProduct_ProductIdAndWarehouse_WarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found"));

        inventory.setQuantityReserved(Math.max(0, inventory.getQuantityReserved() - quantity));
        inventoryRepository.save(inventory);
    }

    private void createTransaction(Product product, Warehouse warehouse,
                                   InventoryTransaction.TransactionType type,
                                   Integer quantityChange, Integer quantityBefore,
                                   Integer quantityAfter, String notes) {
        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .warehouse(warehouse)
                .transactionType(type)
                .quantityChange(quantityChange)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .notes(notes)
                .build();

        transactionRepository.save(transaction);
    }

    private InventoryResponse convertToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId())
                .productId(inventory.getProduct().getProductId())
                .productName(inventory.getProduct().getProductName())
                .sku(inventory.getProduct().getSku())
                .warehouseId(inventory.getWarehouse().getWarehouseId())
                .warehouseName(inventory.getWarehouse().getWarehouseName())
                .quantityOnHand(inventory.getQuantityOnHand())
                .quantityReserved(inventory.getQuantityReserved())
                .quantityAvailable(inventory.getQuantityAvailable())
                .binLocation(inventory.getBinLocation())
                .lastRestockDate(inventory.getLastRestockDate())
                .lastCountDate(inventory.getLastCountDate())
                .reorderPoint(inventory.getProduct().getReorderPoint())
                .needsReorder(inventory.getQuantityOnHand() <= inventory.getProduct().getReorderPoint())
                .build();
    }
}
