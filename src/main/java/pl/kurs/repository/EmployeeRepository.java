package pl.kurs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE (e.firstName = :firstName OR :firstName IS NULL) " +
           "AND (e.lastName = :lastName OR :lastName IS NULL) " +
           "AND (e.position.name = :position OR :position IS NULL)")
    Page<Employee> findAllByFirstNameAndLastNameAndPosition(String firstName, String lastName, String position, Pageable pageable);
}
