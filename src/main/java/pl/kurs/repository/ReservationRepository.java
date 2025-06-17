package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Reservation;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        select case when count(r) > 0 then true else false end
        from Reservation r
        where r.car.id = :carId
          and r.startDate <= :endDate
          and r.endDate   >= :startDate
    """)
    boolean existsOverlap(Long carId, LocalDate startDate, LocalDate endDate);

}
