package com.carmarket.model;

import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "car_id")
    private Long carId;
    @Column(name = "car_company")
    private String carCompany;
    @Column(name = "car_model")
    private String carModel;
    @Column(name = "car_release_year")
    private int carReleaseYear;
    @Column(name = "car_mileage")
    private int carMileage;
    @Enumerated(EnumType.STRING)
    @Column(name = "car_type")
    private CarType carType;
    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type")
    private EngineType engineType;
    @Column(name = "car_crushed")
    private boolean carCrushed;
    @Column(name = "car_price")
    private Long carPrice;
    @Column(name = "car_image")
    private String carImage;
    @Column(name = "car_sold")
    private boolean carSold;
    @Column(name = "car_published")
    private LocalDate carPublished;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JsonIgnore
    private Set<Customer> carLikes = new HashSet<>();

    @Builder
    public Car(String carCompany, String carModel, int carReleaseYear, int carMileage,
               CarType carType, EngineType engineType, boolean carCrushed, Long carPrice, String carImage,
               boolean carSold, LocalDate carPublished, Customer customer, Set<Customer> carLikes) {
        super();
        this.carCompany = carCompany;
        this.carModel = carModel;
        this.carReleaseYear = carReleaseYear;
        this.carMileage = carMileage;
        this.carType = carType;
        this.engineType = engineType;
        this.carCrushed = carCrushed;
        this.carPrice = carPrice;
        this.carImage = carImage;
        this.carSold = carSold;
        this.carPublished = carPublished;
        this.customer = customer;
        this.carLikes = carLikes;
    }

    public Car addCustomerToCarSet(Customer customer) {
        this.carLikes.add(customer);
        return this;
    }

    public Car removeCustomerFromCarSet(Customer customer) {
        this.carLikes.remove(customer);
        return this;
    }
}
