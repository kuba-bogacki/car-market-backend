package com.carmarket.service.implementation;

import com.carmarket.model.Car;
import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.carmarket.repository.CarRepository;
import com.carmarket.service.CarService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class CarServiceImplementation implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImplementation(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public void addNewCar(String carCompany, String carModel, int carReleaseYear, int carMileage, CarType carType,
                          EngineType engineType, boolean carCrushed, Long carPrice, String carImage) {
        Car car = Car.builder()
                .carCompany(carCompany)
                .carModel(carModel)
                .carReleaseYear(carReleaseYear)
                .carMileage(carMileage)
                .carType(carType)
                .engineType(engineType)
                .carCrushed(carCrushed)
                .carPrice(carPrice)
                .carImage(carImage)
                .carSold(false)
                .build();
        carRepository.save(car);
    }

    @Override
    public void uploadImage(String imageDirectory, MultipartFile multipartFile, String imageName) throws IOException {
        File directory = new File(imageDirectory + imageName);
        multipartFile.transferTo(directory);
    }
}
