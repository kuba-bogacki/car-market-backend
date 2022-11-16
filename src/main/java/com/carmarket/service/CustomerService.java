package com.carmarket.service;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface CustomerService extends UserDetailsService {
    void createAfterStart(String customerFirstName, String customerLastName,
                          String customerEmail, String customerPassword);
    Optional<Customer> selectCustomerByCustomerEmail(String customerEmail);
    void createNewCustomer(String customerFirstName, String customerLastName,
                           String customerEmail, String customerPassword);
    void addCarToCustomerFavourites(Customer customer, Car car);
    void removeCarFromCustomerFavourites(Customer customer, Car car);
}
