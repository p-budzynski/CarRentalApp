package pl.kurs.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.ReservationDto;
import pl.kurs.dto.ReservationDtoList;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.mapper.ReservationMapper;
import pl.kurs.service.CarService;
import pl.kurs.service.CustomerService;
import pl.kurs.service.ReservationService;
import pl.kurs.service.StatusService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

import java.util.List;

@Validated
@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";
    private static final int MAX_SIZE = 100;

    private ReservationService reservationService;
    private ReservationMapper reservationMapper;
    private CarService carService;
    private CustomerService customerService;
    private StatusService statusService;

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReservationDto> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.entityToDto(reservation));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<ReservationDto> getAll(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Reservation> reservations = reservationService.getAll(page, size);
        return reservations.map(reservationMapper::entityToDto);
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ReservationDtoList getAllXml(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Reservation> reservations = reservationService.getAll(page, size);
        List<ReservationDto> dtos = reservationMapper.entitiesToDtos(reservations.getContent());
        return new ReservationDtoList(dtos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto createReservation(@RequestBody @Validated(Create.class) ReservationDto reservationDto) {
        Car car = carService.getCarById(reservationDto.getCarId());
        Customer customer = customerService.getCustomerById(reservationDto.getCustomerId());
        Status status = statusService.getStatusById(reservationDto.getStatusId());
        Reservation reservation = reservationMapper.dtoToEntity(reservationDto);
        reservation.setCar(car);
        reservation.setCustomer(customer);
        reservation.setStatus(status);
        Reservation savedReservation = reservationService.saveReservation(reservation);
        return reservationMapper.entityToDto(savedReservation);
    }

    @PutMapping
    public ReservationDto updateReservation(@RequestBody @Validated(Update.class) ReservationDto reservationDto) {
        Car car = carService.getCarById(reservationDto.getCarId());
        Customer customer = customerService.getCustomerById(reservationDto.getCustomerId());
        Status status = statusService.getStatusById(reservationDto.getStatusId());
        Reservation reservation = reservationMapper.dtoToEntityWithId(reservationDto);
        reservation.setCar(car);
        reservation.setCustomer(customer);
        reservation.setStatus(status);
        Reservation updatedReservation = reservationService.updateReservation(reservation);
        return reservationMapper.entityToDto(updatedReservation);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservationById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Reservation reservation = reservationService.cancelReservationById(id);
        return ResponseEntity.ok(reservationMapper.entityToDto(reservation));
    }

}
