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
import pl.kurs.entity.Reservation;
import pl.kurs.mapper.ReservationMapper;
import pl.kurs.service.ReservationService;
import pl.kurs.validation.Create;
import pl.kurs.validation.Update;

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

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReservationDto> getById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Reservation reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservationMapper.entityToDto(reservation));
    }

    @GetMapping(produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Page<ReservationDto> getAll(
            @RequestParam(defaultValue = DEFAULT_PAGE) @Min(0) int page,
            @RequestParam(defaultValue = DEFAULT_SIZE) @Min(1) @Max(MAX_SIZE) int size) {

        Page<Reservation> reservations = reservationService.getAll(page, size);
        return reservations.map(reservationMapper::entityToDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDto createReservation(@RequestBody @Validated(Create.class) ReservationDto reservationDto) {
        Reservation reservation = reservationService.createReservation(reservationDto);
        Reservation savedReservation = reservationService.saveReservation(reservation);
        return reservationMapper.entityToDto(savedReservation);
    }

    @PutMapping
    public ReservationDto updateReservation(@RequestBody @Validated(Update.class) ReservationDto reservationDto) {
        Reservation updatedReservation = reservationService.updateReservation(reservationDto);
        return reservationMapper.entityToDto(updatedReservation);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ReservationDto> cancelReservationById(@PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        Reservation reservation = reservationService.cancelReservationById(id);
        return ResponseEntity.ok(reservationMapper.entityToDto(reservation));
    }

}
