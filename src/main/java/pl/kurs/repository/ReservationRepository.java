package pl.kurs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Override
    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.car " +
                   "LEFT JOIN FETCH r.customer LEFT JOIN FETCH r.status")
    Page<Reservation> findAll(Pageable pageable);
}
