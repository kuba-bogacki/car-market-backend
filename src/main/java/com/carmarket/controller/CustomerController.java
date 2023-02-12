package com.carmarket.controller;

import com.carmarket.jwt.JwtConfiguration;
import com.carmarket.jwt.JwtTokenResponse;
import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.repository.CarRepository;
import com.carmarket.service.CustomerService;
import com.carmarket.service.EmailService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.carmarket.model.type.CustomerRole.ADMIN;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
    private final CustomerService customerService;
    private final CarRepository carRepository;
    private final JwtConfiguration jwtConfiguration;
    private final EmailService emailService;

    @Autowired
    public CustomerController(CustomerService customerService, CarRepository carRepository,
                              JwtConfiguration jwtConfiguration, EmailService emailService) {
        this.customerService = customerService;
        this.carRepository = carRepository;
        this.jwtConfiguration = jwtConfiguration;
        this.emailService = emailService;
    }

    @GetMapping(value = "/get-current-customer")
    public ResponseEntity<?> getCustomerByJwt(@RequestHeader("Authorization") String token) {
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        if (currentCustomer.isPresent()) {
            return ResponseEntity.ok(currentCustomer.get());
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get-all-customers")
    public ResponseEntity<?> getAllCustomers(@RequestHeader("Authorization") String token) {
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        if (currentCustomer.isPresent() && currentCustomer.get().getAuthorities().equals(ADMIN.getGrantedAuthorities())) {
            List<Customer> customerList = customerService.getAllCustomers();
            return new ResponseEntity<>(customerList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody Customer customer) {
        if (customerService.selectCustomerByCustomerEmail(customer.getCustomerEmail()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            customerService.createNewCustomer(customer.getCustomerFirstName(), customer.getCustomerLastName(),
                    customer.getCustomerEmail(), customer.getPassword());
            final UserDetails userDetails = customerService.loadUserByUsername(customer.getCustomerEmail());
            final String token = jwtConfiguration.generateToken(userDetails);
            return ResponseEntity.ok(new JwtTokenResponse(token));
        }
    }

    @PutMapping(value = "/send-email-to-reset-password")
    public ResponseEntity<?> sendEmailWithResetPasswordLink(@RequestBody ObjectNode objectNode)
            throws MessagingException, IOException {
        Optional<Customer> currentCustomer =
            customerService.selectCustomerByCustomerEmail(objectNode.get("customerEmail").asText());
        if (currentCustomer.isPresent()) {
            emailService.sendVerificationEmail(currentCustomer.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/reset-password")
    public ResponseEntity<?> resetCustomerPassword(@RequestBody ObjectNode objectNode) {
        Optional<Customer> currentCustomer =
            customerService.selectCustomerByCustomerEmail(objectNode.get("customerEmail").asText());
        if (currentCustomer.isPresent() &&
            currentCustomer.get().getCustomerVerificationCode().equals(objectNode.get("verificationCode").asText())) {
            Customer modifiedCustomer =
                customerService.resetCustomerPassword(currentCustomer.get(), objectNode.get("customerPassword").asText());
            final UserDetails userDetails = customerService.loadUserByUsername(modifiedCustomer.getCustomerEmail());
            final String token = jwtConfiguration.generateToken(userDetails);
            return new ResponseEntity<>(new JwtTokenResponse(token), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/add-car-to-favourite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCarToFavourite(@RequestBody Car car, @RequestHeader("Authorization") String token) {
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        Optional<Car> currentCar = Optional.ofNullable(carRepository.getCarByCarId(car.getCarId()));
        if (currentCustomer.isPresent() && currentCar.isPresent()) {
            customerService.addCarToCustomerFavourites(currentCustomer.get(), currentCar.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/remove-car-from-favourite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeCarFromFavourite(@RequestBody Car car, @RequestHeader("Authorization") String token) {
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        Optional<Car> currentCar = Optional.ofNullable(carRepository.getCarByCarId(car.getCarId()));
        if (currentCustomer.isPresent() && currentCar.isPresent()) {
            customerService.removeCarFromCustomerFavourites(currentCustomer.get(), currentCar.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/create-admin")
    public void createAdminAccount() {
        customerService.createAdminAccount();
    }
}
