package pl.kurs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE (c.firstName = :firstName OR :firstName IS NULL) AND (c.lastName = :lastName OR :lastName IS NULL)")
    Page<Customer> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);
}
