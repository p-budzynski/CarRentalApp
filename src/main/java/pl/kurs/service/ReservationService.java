package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.kurs.dto.ReservationDto;
import pl.kurs.entity.Car;
import pl.kurs.entity.Customer;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.exception.ConflictException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.ReservationMapper;
import pl.kurs.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final CustomerService customerService;
    private final CarService carService;

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    public Page<Reservation> getAll(int page, int size) {
        return reservationRepository.findAll(PageRequest.of(page, size));
    }

    @Transactional
    public Reservation saveReservation(Reservation reservation) {
        Car car = carService.findForUpdate(reservation.getCar().getId());

        if (reservationRepository.existsOverlap(car.getId(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new ConflictException("Car already booked for this date");
        }
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservation(ReservationDto reservationDto) {
        Reservation existingReservation = getReservationById(reservationDto.getId());
        Car car = carService.findForUpdate(reservationDto.getCarId());

        if (reservationRepository.existsOverlapExcludingReservation(
                car.getId(),
                reservationDto.getStartDate(),
                reservationDto.getEndDate(),
                reservationDto.getId()
        )) {
            throw new ConflictException("Car already booked for this date");
        }

        existingReservation.setStatus(Status.CANCELED);
        reservationRepository.save(existingReservation);

        Reservation newReservation = createReservation(reservationDto);
        newReservation.setId(null);
        return reservationRepository.save(newReservation);
    }

    public Reservation cancelReservationById(Long id) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(Status.CANCELED);
        return reservationRepository.save(reservation);
    }

    public Reservation createReservation(ReservationDto reservationDto) {
        Car car = carService.getCarById(reservationDto.getCarId());
        Customer customer = customerService.getCustomerById(reservationDto.getCustomerId());
        Reservation reservation = reservationMapper.dtoToEntityWithId(reservationDto);
        reservation.setCar(car);
        reservation.setCustomer(customer);
        reservation.setStatus(Status.fromString(reservationDto.getStatusName()));
        return reservation;
    }

    public List<Reservation> findActiveReservationForDate(LocalDate startDate) {
        return reservationRepository.findActiveReservationForDate(startDate);
    }
}
