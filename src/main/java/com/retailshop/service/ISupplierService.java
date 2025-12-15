package com.retailshop.service;

import com.retailshop.entity.Supplier;
import java.util.List;

public interface ISupplierService {
    Supplier createSupplier(Supplier supplier);
    Supplier updateSupplier(Long supplierId, Supplier supplier);
    Supplier getSupplierById(Long supplierId);
    List<Supplier> getAllSuppliers();
    List<Supplier> getActiveSuppliers();
    void deleteSupplier(Long supplierId);
}
