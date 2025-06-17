package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Car;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.exception.ConflictException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.ReservationRepository;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Service
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StatusService statusService;
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
    public Reservation updateReservation(Reservation reservation) {
        Reservation reservationToUpdate = getReservationById(reservation.getId());
        Car car = carService.findForUpdate(reservation.getCar().getId());

        if (reservationRepository.existsOverlap(car.getId(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new ConflictException("Car already booked for this date");
        }
        BeanUtils.copyProperties(reservation, reservationToUpdate);
        return reservationRepository.save(reservationToUpdate);
    }

    public Reservation cancelReservationById(Long id) {
        Reservation reservation = getReservationById(id);
        Status cancelledStatus = statusService.findByName("CANCELLED");
        reservation.setStatus(cancelledStatus);
        return reservationRepository.save(reservation);
    }

}
