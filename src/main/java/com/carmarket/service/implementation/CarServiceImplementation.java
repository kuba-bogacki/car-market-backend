package com.carmarket.service.implementation;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.carmarket.repository.CarRepository;
import com.carmarket.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
class CarServiceImplementation implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImplementation(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll().stream().filter(e -> !e.isCarSold()).toList();
    }

    @Override
    public Car getCarById(Long carId) {
        return carRepository.getCarByCarId(carId);
    }

    @Override
    public void addNewCar(String carCompany, String carModel, int carReleaseYear, int carMileage, CarType carType,
                          EngineType engineType, boolean carCrushed, Long carPrice, String carImage, Customer customer) {
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
                .carPublished(LocalDate.now())
                .customer(customer)
                .carLikes(new HashSet<>())
                .build();
        carRepository.save(car);
    }

    @Override
    public void uploadImage(String imageDirectory, MultipartFile multipartFile, String imageName) throws IOException {
        File directory = new File(imageDirectory + imageName);
        multipartFile.transferTo(directory);
    }

    @Override
    public void changeCarStatusAndOwner(Long carId, Customer customer) {
        Car car = carRepository.getCarByCarId(carId);
        car.setCarSold(true);
        car.setCustomer(customer);
        carRepository.save(car);
    }
}
