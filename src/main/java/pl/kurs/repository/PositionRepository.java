package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.entity.Position;

public interface PositionRepository extends JpaRepository<Position, Long> {


}
