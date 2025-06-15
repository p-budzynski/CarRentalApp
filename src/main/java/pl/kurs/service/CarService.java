package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Car;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.InvalidDataAccessApiUsageException;
import pl.kurs.repository.CarRepository;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public Page<Car> getAll(int page, int size) {
        return carRepository.findAll(PageRequest.of(page, size));
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    @Transactional
    public Car updateCar(Car car) {
        Car carToUpdate = carRepository.findById(car.getId())
                .orElseThrow(() -> new DataNotFoundException("Car with id: " + car.getId() + " not found"));
        BeanUtils.copyProperties(car, carToUpdate);
        return carRepository.save(carToUpdate);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    public Page<Car> getAllSorted(String property, Sort.Direction direction, int page, int size) {

        List<String> allowedProperties = Arrays.stream(Car.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        if (!allowedProperties.contains(property)) {
            throw new InvalidDataAccessApiUsageException("Invalid sort field: " + property);
        }

        Sort sort = Sort.by(direction, property);
        Pageable pageable = PageRequest.of(page, size, sort);
        return carRepository.findAll(pageable);
    }

    public Page<Car> getByProducerAndModelAndAvailable(String producer, String model, LocalDate startDate, LocalDate endDate, int page, int size) {
        if (startDate == null || endDate == null) {
            return carRepository.findByProducerAndModel(producer, model, PageRequest.of(page, size));
        }

        return carRepository.findAvailableCars(producer, model, startDate, endDate, PageRequest.of(page, size));
    }


}

