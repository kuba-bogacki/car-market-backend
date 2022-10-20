package com.carmarket.service.implementation;

import com.carmarket.model.Customer;
import com.carmarket.repository.CustomerRepository;
import com.carmarket.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.carmarket.model.type.CustomerRole.ADMIN;
import static com.carmarket.model.type.CustomerRole.USER;

@Service
public class CustomerServiceImplementation implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceImplementation(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createAfterStart(String customerFirstName, String customerLastName,
                                 String customerEmail, String customerPassword) {
        Customer customer = Customer.builder()
                .customerFirstName("Kuba")
                .customerLastName("Bogacki")
                .customerEmail("kubik@wp.pl")
                .customerPassword(passwordEncoder.encode("admin"))
                .authorities(ADMIN.getGrantedAuthorities())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public Customer loadUserByCustomerEmail(String customerEmail) throws UsernameNotFoundException {
        return customerRepository.findCustomerByCustomerEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", customerEmail)));
    }

    @Override
    public UserDetails loadUserByUsername(String customerEmail) throws UsernameNotFoundException {
        return customerRepository.findCustomerByCustomerEmail(customerEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", customerEmail)));
    }

    @Override
    public Optional<Customer> selectCustomerByCustomerEmail(String customerEmail) {
        return customerRepository.findAll()
                .stream()
                .filter(applicationUser -> customerEmail.equals(applicationUser.getCustomerEmail()))
                .findFirst();
    }

    @Override
    public void createNewCustomer(String customerFirstName, String customerLastName,
                                  String customerEmail, String customerPassword) {
        Customer customer = Customer.builder()
                .customerFirstName(customerFirstName)
                .customerLastName(customerLastName)
                .customerPassword(passwordEncoder.encode(customerPassword))
                .customerEmail(customerEmail)
                .authorities(USER.getGrantedAuthorities())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        customerRepository.save(customer);
    }
}
