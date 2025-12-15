package com.retailshop.repository;

import com.retailshop.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerCode(String customerCode);
    List<Customer> findByIsActiveTrue();
    Boolean existsByCustomerCode(String customerCode);
    Optional<Customer> findByEmail(String email);
}
