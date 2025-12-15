package com.retailshop.service;

import com.retailshop.entity.Customer;
import com.retailshop.exception.DuplicateResourceException;
import com.retailshop.exception.ResourceNotFoundException;
import com.retailshop.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByCustomerCode(customer.getCustomerCode())) {
            throw new DuplicateResourceException("Customer code already exists");
        }
        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long customerId, Customer customer) {
        Customer existing = getCustomerById(customerId);
        existing.setCustomerName(customer.getCustomerName());
        existing.setContactPerson(customer.getContactPerson());
        existing.setEmail(customer.getEmail());
        existing.setPhone(customer.getPhone());
        existing.setAddress(customer.getAddress());
        existing.setCity(customer.getCity());
        existing.setState(customer.getState());
        existing.setZipCode(customer.getZipCode());
        existing.setCountry(customer.getCountry());
        existing.setCustomerType(customer.getCustomerType());
        existing.setCreditLimit(customer.getCreditLimit());
        existing.setIsActive(customer.getIsActive());
        return customerRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional
    public void deleteCustomer(Long customerId) {
        Customer customer = getCustomerById(customerId);
        customerRepository.delete(customer);
    }
}
