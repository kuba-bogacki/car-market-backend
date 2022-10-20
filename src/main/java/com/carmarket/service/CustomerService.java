package com.carmarket.service;

import com.carmarket.model.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface CustomerService extends UserDetailsService {
    Customer loadUserByCustomerEmail(String customerEmail) throws UsernameNotFoundException;

    void createAfterStart(String customerFirstName, String customerLastName,
                          String customerEmail, String customerPassword);
    Optional<Customer> selectCustomerByCustomerEmail(String customerEmail);
    void createNewCustomer(String customerFirstName, String customerLastName,
                           String customerEmail, String customerPassword);
}
