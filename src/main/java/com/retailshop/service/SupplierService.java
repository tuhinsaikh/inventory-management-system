package com.retailshop.service;

import com.retailshop.entity.Supplier;
import com.retailshop.exception.DuplicateResourceException;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        if (supplierRepository.existsBySupplierCode(supplier.getSupplierCode())) {
            throw new DuplicateResourceException("Supplier code already exists");
        }
        return supplierRepository.save(supplier);
    }

    @Override
    @Transactional
    public Supplier updateSupplier(Long supplierId, Supplier supplier) {
        Supplier existing = getSupplierById(supplierId);
        existing.setSupplierName(supplier.getSupplierName());
        existing.setContactPerson(supplier.getContactPerson());
        existing.setEmail(supplier.getEmail());
        existing.setPhone(supplier.getPhone());
        existing.setAddress(supplier.getAddress());
        existing.setCity(supplier.getCity());
        existing.setState(supplier.getState());
        existing.setZipCode(supplier.getZipCode());
        existing.setCountry(supplier.getCountry());
        existing.setPaymentTerms(supplier.getPaymentTerms());
        existing.setLeadTimeDays(supplier.getLeadTimeDays());
        existing.setRating(supplier.getRating());
        existing.setIsActive(supplier.getIsActive());
        return supplierRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Supplier getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void deleteSupplier(Long supplierId) {
        Supplier supplier = getSupplierById(supplierId);
        supplierRepository.delete(supplier);
    }
}
