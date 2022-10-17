package com.carmarket.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_first_name")
    private String customerFirstName;
    @Column(name = "customer_last_name")
    private String customerLastName;
    @Column(name = "customer_email", unique = true)
    private String customerEmail;
    @Column(name = "customer_password")
    private String customerPassword;
    @OneToMany
    @Column(name = "customer_cars_list")
    private List<Car> customerCarsList = new ArrayList<>();

    @Builder
    public Customer(String customerFirstName, String customerLastName, String customerEmail, String customerPassword) {
        super();
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerEmail = customerEmail;
        this.customerPassword = customerPassword;
    }
}
