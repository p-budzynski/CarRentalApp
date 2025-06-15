package pl.kurs.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Reservation;
import pl.kurs.entity.Status;
import pl.kurs.exception.DataNotFoundException;
import pl.kurs.exception.StatusNotFoundException;
import pl.kurs.repository.ReservationRepository;
import pl.kurs.repository.StatusRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StatusRepository statusRepository;

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Page<Reservation> getAll(int page, int size) {
        return reservationRepository.findAll(PageRequest.of(page, size));
    }

    public Reservation saveReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservation(Reservation reservation) {
        Reservation reservationToUpdate = reservationRepository.findById(reservation.getId())
                .orElseThrow(() -> new DataNotFoundException("Reservation with id: " + reservation.getId() + " not found"));
        BeanUtils.copyProperties(reservation, reservationToUpdate);
        return reservationRepository.save(reservationToUpdate);
    }

    public Optional<Reservation> cancelReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(reservation -> {
                    Status cancelledStatus = statusRepository.findByName("CANCELLED")
                            .orElseThrow(() -> new StatusNotFoundException("Status CANCELLED not found"));

                    reservation.setStatus(cancelledStatus);
                    return reservationRepository.save(reservation);
                });
    }

}
