package com.carmarket.repository;

import com.carmarket.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Car getCarByCarId(Long carId);
}
