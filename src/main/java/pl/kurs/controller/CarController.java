package pl.kurs.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.CarDto;
import pl.kurs.dto.CarDtoList;
import pl.kurs.entity.Car;
import pl.kurs.mapper.CarMapper;
import pl.kurs.service.CarService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/cars")
@AllArgsConstructor
public class CarController {
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final int MAX_SIZE = 100;

    private CarService carService;
    private CarMapper carMapper;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CarDto> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(carMapper.entityToDto(car));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CarDto> getAll(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Car> cars = carService.getAll(page, size);
        return cars.map(carMapper::entityToDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CarDtoList getAllXml(@RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
                                @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Car> cars = carService.getAll(page, size);
        List<CarDto> dtos = carMapper.entitiesToDtos(cars.getContent());
        return new CarDtoList(dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto createCar(@RequestBody @Validated(Create.class) CarDto carDto) {
        Car car = carMapper.dtoToEntity(carDto);
        Car savedCar = carService.saveCar(car);
        return carMapper.entityToDto(savedCar);
    }

    @PutMapping
    public CarDto updateCar(@RequestBody @Validated(Update.class) CarDto carDto) {
        Car car = carMapper.dtoToEntityWithId(carDto);
        Car updatedCar = carService.updateCar(car);
        return carMapper.entityToDto(updatedCar);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        carService.deleteCarById(id);
    }

    @GetMapping(value = "/search", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Page<CarDto> getByParams(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size,
            @RequestParam(value = "producer", required = false) String producer,
            @RequestParam(value = "model", required = false) String model,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Page<Car> cars = carService.getByProducerAndModelAndAvailable(producer, model, startDate, endDate, page, size);
        return cars.map(carMapper::entityToDto);
    }

    @GetMapping(value = "/sort", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Page<CarDto> getAllSortedByParams(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size,
            @RequestParam(value = "property", defaultValue = "id") String property,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<Car> cars = carService.getAllSorted(property, sortDirection, page, size);
        return cars.map(carMapper::entityToDto);
    }
}
