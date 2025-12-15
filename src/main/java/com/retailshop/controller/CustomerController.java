package com.retailshop.controller;

import com.retailshop.dto.response.ApiResponse;
import com.retailshop.entity.Customer;
import com.retailshop.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        ApiResponse<List<Customer>> response = ApiResponse.<List<Customer>>builder()
                .success(true)
                .message("Customers retrieved successfully")
                .data(customers)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        ApiResponse<Customer> response = ApiResponse.<Customer>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(customer)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        ApiResponse<Customer> response = ApiResponse.<Customer>builder()
                .success(true)
                .message("Customer created successfully")
                .data(createdCustomer)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long customerId,
            @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(customerId, customer);
        ApiResponse<Customer> response = ApiResponse.<Customer>builder()
                .success(true)
                .message("Customer updated successfully")
                .data(updatedCustomer)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Customer deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
