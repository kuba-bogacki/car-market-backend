package com.carmarket.controller;

import com.carmarket.jwt.JwtConfiguration;
import com.carmarket.model.Customer;
import com.carmarket.model.Payment;
import com.carmarket.model.type.Currency;
import com.carmarket.service.CarService;
import com.carmarket.service.CustomerService;
import com.carmarket.service.PaymentService;
import com.carmarket.stripe.PaymentConfiguration;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.carmarket.model.type.CustomerRole.ADMIN;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    private final PaymentService paymentService;
    private final CustomerService customerService;
    private final CarService carService;
    private final PaymentConfiguration paymentConfiguration;
    private final JwtConfiguration jwtConfiguration;

    @Autowired
    public PaymentController(PaymentService paymentService, CustomerService customerService, CarService carService,
                             PaymentConfiguration paymentConfiguration, JwtConfiguration jwtConfiguration) {
        this.paymentService = paymentService;
        this.customerService = customerService;
        this.carService = carService;
        this.paymentConfiguration = paymentConfiguration;
        this.jwtConfiguration = jwtConfiguration;
    }

    @GetMapping(value = "/get-publish-stripe-key")
    public ResponseEntity<String> getPublishStripeKey() {
        String publicKey = paymentConfiguration.getStripePublicKey();
        return ResponseEntity.ok(publicKey);
    }

    @GetMapping(value = "/get-all-payments")
    public ResponseEntity<?> getaAllPaymentTransactions(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        if (currentCustomer.isPresent() && currentCustomer.get().getAuthorities().equals(ADMIN.getGrantedAuthorities())) {
            List<Payment> paymentList = paymentService.getAllTransactions();
            return new ResponseEntity<>(paymentList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(value = "/charge-customer")
    public ResponseEntity<?> chargeCustomer(@RequestHeader("Authorization") String token, @RequestBody ObjectNode objectNode)
            throws StripeException {
        Optional<Customer> currentCustomer =
                customerService.selectCustomerByCustomerEmail(jwtConfiguration.getUsernameFromToken(token.substring(7)));
        Optional<Charge> charge = Optional.ofNullable(paymentService.chargeCustomerCard(objectNode.get("paymentAmount").asLong(),
                Currency.USD, currentCustomer.get().getCustomerEmail(), objectNode.get("paymentToken").asText()));
        if (charge.isPresent()) {
            carService.changeCarStatusAndOwner(objectNode.get("carId").asLong(), currentCustomer.get());
            return new ResponseEntity<>(charge.get().getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
