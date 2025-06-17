package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Position;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.PositionRepository;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionRepository positionRepository;

    public Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position not found with id: " + id));
    }
}
