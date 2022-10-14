package com.carmarket.model;

import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

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

    @Builder
    public Car(String carCompany, String carModel, int carReleaseYear, int carMileage,
               CarType carType, EngineType engineType, boolean carCrushed, Long carPrice,
               String carImage, boolean carSold) {
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
    }
}
