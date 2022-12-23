package com.carmarket.service;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface CustomerService extends UserDetailsService {
    Optional<Customer> selectCustomerByCustomerEmail(String customerEmail);
    List<Customer> getAllCustomers();
    void createNewCustomer(String customerFirstName, String customerLastName,
                           String customerEmail, String customerPassword);
    void addCarToCustomerFavourites(Customer customer, Car car);
    void removeCarFromCustomerFavourites(Customer customer, Car car);
    void createAdminAccount();
}
