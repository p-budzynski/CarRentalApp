package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
                select case when count(r) > 0 then true else false end
                from Reservation r
                where r.car.id = :carId
                  and r.startDate <= :endDate
                  and r.endDate   >= :startDate
            """)
    boolean existsOverlap(Long carId, LocalDate startDate, LocalDate endDate);

    @Query("""
                select case when count(r) > 0 then true else false end
                from Reservation r
                where r.car.id = :carId
                and r.status != 'CANCELED'
                and r.id != :excludeId
                and (r.startDate <= :endDate and r.endDate >= :startDate)
            """)
    boolean existsOverlapExcludingReservation(Long carId, LocalDate startDate, LocalDate endDate, Long excludeId);

    @Query("SELECT r FROM Reservation r WHERE r.startDate = :startDate AND r.status IN ('RESERVED')")
    List<Reservation> findActiveReservationForDate(LocalDate startDate);
}
