package com.carmarket.service;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface CarService {
    List<Car> getAllCars();
    void addNewCar(String carCompany, String carModel, int carReleaseYear, int carMileage, CarType carType,
                   EngineType engineType, boolean carCrushed, Long carPrice, String carImage, Customer customer);
    void uploadImage(String imageDirectory, MultipartFile multipartFile, String imageName) throws IOException;
    Car getCarById(Long carId);
    void changeCarStatusAndOwner(Long carId, Customer customer);
    Set<Car> getAdvancedSearchCarList(ObjectNode objectNode);
}
