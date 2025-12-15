package com.retailshop.service;

import com.retailshop.entity.Customer;
import java.util.List;

public interface ICustomerService {
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long customerId, Customer customer);
    Customer getCustomerById(Long customerId);
    List<Customer> getAllCustomers();
    List<Customer> getActiveCustomers();
    void deleteCustomer(Long customerId);
}
