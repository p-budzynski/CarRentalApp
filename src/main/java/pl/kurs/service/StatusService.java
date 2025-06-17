package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.entity.Status;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.exception.StatusNotFoundException;
import pl.kurs.repository.StatusRepository;

@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Status getStatusById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status not found with id: " + id));
    }

    public Status findByName(String cancelled) {
        return statusRepository.findByName(cancelled)
                .orElseThrow(() -> new StatusNotFoundException("Status CANCELLED not found"));
    }
}
