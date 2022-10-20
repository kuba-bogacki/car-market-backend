package com.carmarket.controller;

import com.carmarket.model.Car;
import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.carmarket.service.CarService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static com.carmarket.utils.Directory.IMAGE_DIRECTORY;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {

    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping(value = "/cars")
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    @GetMapping(value = "/get-car/{carId}")
    public Car getCarById(@PathVariable("carId") Long carId) {
        return carService.getCarById(carId);
    }

    @GetMapping(value = "/get-car-image/{imageName}")
    public String getCarImage(@PathVariable("imageName") String imageName) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(IMAGE_DIRECTORY + imageName));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    @PostMapping(value = "/add-new-car")
    public ResponseEntity<?> addNewCar(@RequestParam("loadedImage") MultipartFile multipartFile,
            @RequestParam("carCompany") String carCompany, @RequestParam("carModel") String carModel,
            @RequestParam("carReleaseYear") String carReleaseYear, @RequestParam("carMileage") String carMileage,
            @RequestParam("carPrice") String carPrice, @RequestParam("carType") String carType,
            @RequestParam("engineType") String engineType, @RequestParam("crushed") String crushed) {
        try {
            carService.uploadImage(IMAGE_DIRECTORY, multipartFile, multipartFile.getOriginalFilename());
            carService.addNewCar(carCompany, carModel, Integer.parseInt(carReleaseYear), Integer.parseInt(carMileage),
                    CarType.valueOf(carType.toUpperCase()), EngineType.valueOf(engineType.toUpperCase()),
                    Boolean.parseBoolean(crushed), Long.parseLong(carPrice), multipartFile.getOriginalFilename());
            return new ResponseEntity<>(multipartFile.getOriginalFilename(), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Image not updated", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
