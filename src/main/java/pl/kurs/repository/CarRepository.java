package pl.kurs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Car;

import java.time.LocalDate;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE (c.producer = :producer OR :producer IS NULL) AND (c.model = :model OR :model IS NULL)")
    Page<Car> findByProducerAndModel(String producer, String model, Pageable pageable);

    @Query("SELECT c FROM Car c WHERE (c.producer = :producer OR :producer IS NULL) AND (c.model = :model OR :model IS NULL) " +
           "AND c.id NOT IN (SELECT r.car.id FROM Reservation r WHERE " +
           "r.status.id IN (SELECT s.id FROM Status s WHERE s.name IN ('CONFIRMED', 'ACTIVE')) " +
           "AND r.startDate < :endDate AND r.endDate > :startDate)")
    Page<Car> findAvailableCars(String producer, String model, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
