package com.carmarket.service.implementation;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.repository.CarRepository;
import com.carmarket.repository.CustomerRepository;
import com.carmarket.service.CustomerService;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.carmarket.model.type.CustomerRole.ADMIN;
import static com.carmarket.model.type.CustomerRole.USER;

@Service
class CustomerServiceImplementation implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CarRepository carRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceImplementation(CustomerRepository customerRepository,
                                         CarRepository carRepository,
                                         PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.carRepository = carRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createAdminAccount() {
        Customer customer = Customer.builder()
                .customerFirstName("Admin")
                .customerLastName("Admin")
                .customerEmail("admin@wp.pl")
                .customerPassword(passwordEncoder.encode("admin"))
                .customerVerificationCode(RandomString.make(64))
                .authorities(ADMIN.getGrantedAuthorities())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        customerRepository.save(customer);
    }

    @Override
    public Customer resetCustomerPassword(Customer customer, String customerPassword) {
        customer.setCustomerPassword(passwordEncoder.encode(customerPassword));
        customer.setCustomerVerificationCode(RandomString.make(64));
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll().stream()
                .sorted(Comparator.comparing(Customer::getCustomerId).reversed())
                .toList();
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
                .customerEmail(customerEmail)
                .customerPassword(passwordEncoder.encode(customerPassword))
                .customerVerificationCode(RandomString.make(64))
                .customerCarsList(new ArrayList<>())
                .customerLikes(new HashSet<>())
                .customerArticles(new ArrayList<>())
                .authorities(USER.getGrantedAuthorities())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
        customerRepository.save(customer);
    }

    @Transactional
    @Override
    public void addCarToCustomerFavourites(Customer customer, Car car) {
        customerRepository.save(customer.addCarToCustomerSet(car));
        carRepository.save(car.addCustomerToCarSet(customer));
    }

    @Transactional
    @Override
    public void removeCarFromCustomerFavourites(Customer customer, Car car) {
        customerRepository.save(customer.removeCarFromCustomerSet(car));
        carRepository.save(car.removeCustomerFromCarSet(customer));
    }
}
