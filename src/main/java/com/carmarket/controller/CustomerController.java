package com.carmarket.controller;

import com.carmarket.jwt.JwtConfiguration;
import com.carmarket.jwt.JwtTokenResponse;
import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.repository.CarRepository;
import com.carmarket.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {
    private final CustomerService customerService;
    private final CarRepository carRepository;
    private final JwtConfiguration jwtConfiguration;

    @Autowired
    public CustomerController(CustomerService customerService, CarRepository carRepository,
                              JwtConfiguration jwtConfiguration) {
        this.customerService = customerService;
        this.carRepository = carRepository;
        this.jwtConfiguration = jwtConfiguration;
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
}
