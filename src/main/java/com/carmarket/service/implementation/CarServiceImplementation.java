package com.carmarket.service.implementation;

import com.carmarket.model.Car;
import com.carmarket.model.Customer;
import com.carmarket.model.type.CarType;
import com.carmarket.model.type.EngineType;
import com.carmarket.repository.CarRepository;
import com.carmarket.service.CarService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.carmarket.model.type.CarType.*;
import static com.carmarket.model.type.EngineType.*;

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

    @Override
    public Set<Car> getAdvancedSearchCarList(ObjectNode objectNode) {

        List<Car> allCarsList = this.getAllCars();
        List<Car> searchedByCarType = new ArrayList<>();
        List<Car> searchedByEngineType = new ArrayList<>();
        List<Car> searchedByCarCrushed = new ArrayList<>();

        boolean sedanCarType = objectNode.get("carTypeArray").get("sedan").asBoolean();
        boolean suvCarType = objectNode.get("carTypeArray").get("suv").asBoolean();
        boolean coupeCarType = objectNode.get("carTypeArray").get("coupe").asBoolean();
        boolean otherCarType = objectNode.get("carTypeArray").get("other").asBoolean();
        if (!sedanCarType && !suvCarType && !coupeCarType && !otherCarType) {
            sedanCarType = true; suvCarType = true; coupeCarType = true; otherCarType = true;
        }
        searchedByCarType.addAll(this.completeCarsByCarType(sedanCarType, SEDAN, allCarsList));
        searchedByCarType.addAll(this.completeCarsByCarType(suvCarType, SUV, allCarsList));
        searchedByCarType.addAll(this.completeCarsByCarType(coupeCarType, COUPE, allCarsList));
        searchedByCarType.addAll(this.completeCarsByCarType(otherCarType, OTHER, allCarsList));

        boolean electricEngineType = objectNode.get("engineTypeArray").get("electric").asBoolean();
        boolean dieselEngineType = objectNode.get("engineTypeArray").get("diesel").asBoolean();
        boolean gasEngineType = objectNode.get("engineTypeArray").get("gas").asBoolean();
        if (!electricEngineType && !dieselEngineType && !gasEngineType) {
            electricEngineType = true; dieselEngineType = true; gasEngineType = true;
        }
        searchedByEngineType.addAll(this.completeCarsByEngineType(electricEngineType, ELECTRIC, searchedByCarType));
        searchedByEngineType.addAll(this.completeCarsByEngineType(dieselEngineType, DIESEL, searchedByCarType));
        searchedByEngineType.addAll(this.completeCarsByEngineType(gasEngineType, GAS, searchedByCarType));

        boolean yesCarCrushed = objectNode.get("crushedArray").get("yes").asBoolean();
        boolean noCarCrushed = objectNode.get("crushedArray").get("no").asBoolean();
        if (!yesCarCrushed && !noCarCrushed) {
            yesCarCrushed = true; noCarCrushed = true;
        }
        searchedByCarCrushed.addAll(this.completeCarsByCarCrushed(yesCarCrushed, true, searchedByEngineType));
        searchedByCarCrushed.addAll(this.completeCarsByCarCrushed(noCarCrushed, false, searchedByEngineType));

        Set<Car> filteredByTypesCarsSet = new HashSet<>(searchedByCarCrushed);

        String carCompanyName = objectNode.get("carCompany").asText().equals("") ? "" :
                objectNode.get("carCompany").asText().substring(0, 1).toUpperCase() +
                objectNode.get("carCompany").asText().substring(1);
        String carModelName = objectNode.get("carModel").asText().equals("") ? "" :
                objectNode.get("carModel").asText().substring(0, 1).toUpperCase() +
                objectNode.get("carModel").asText().substring(1);

        int minCarReleaseYearRange = objectNode.get("carReleaseYearRange").get(0).asInt();
        int maxCarReleaseYearRange = objectNode.get("carReleaseYearRange").get(1).asInt();
        int minCarMileageRange = objectNode.get("carMileageRange").get(0).asInt();
        int maxCarMileageRange = objectNode.get("carMileageRange").get(1).asInt();
        int minCarPriceRange = objectNode.get("carPriceRange").get(0).asInt();
        int maxCarPriceRange = objectNode.get("carPriceRange").get(1).asInt();

        return filteredByTypesCarsSet.stream()
                .filter(e -> carCompanyName.equals("") ? !e.getCarCompany().equals("") : e.getCarCompany().equals(carCompanyName))
                .filter(e -> carModelName.equals("") ? !e.getCarModel().equals("") : e.getCarModel().equals(carModelName))
                .filter(e -> (minCarReleaseYearRange <= e.getCarReleaseYear()) && (e.getCarReleaseYear() <= maxCarReleaseYearRange))
                .filter(e -> (minCarMileageRange <= e.getCarMileage()) && (e.getCarMileage() <= maxCarMileageRange))
                .filter(e -> (minCarPriceRange <= e.getCarPrice()) && (e.getCarPrice() <= maxCarPriceRange))
                .collect(Collectors.toSet());
    }

    private List<Car> completeCarsByCarType(boolean typeIsChosen, CarType carType, List<Car> incomingCarList) {
        List<Car> currentCarsList = new ArrayList<>();
        if (typeIsChosen) {
            currentCarsList = incomingCarList.stream().filter(e -> e.getCarType().equals(carType)).toList();
        }
        return currentCarsList;
    }

    private List<Car> completeCarsByEngineType(boolean typeIsChosen, EngineType engineType, List<Car> incomingCarList) {
        List<Car> currentCarsList = new ArrayList<>();
        if (typeIsChosen) {
            currentCarsList = incomingCarList.stream().filter(e -> e.getEngineType().equals(engineType)).toList();
        }
        return currentCarsList;
    }

    private List<Car> completeCarsByCarCrushed(boolean typeIsChosen, boolean isCrushed, List<Car> incomingCarList) {
        List<Car> currentCarsList = new ArrayList<>();
        if (typeIsChosen) {
            currentCarsList = incomingCarList.stream().filter(e -> e.isCarCrushed() == isCrushed).toList();
        }
        return currentCarsList;
    }
}
