package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.CarRepository;

@RequiredArgsConstructor
public class UniqueRegistrationNumberValidator implements ConstraintValidator<UniqueRegistrationNumber, String> {
private final CarRepository carRepository;

@Override
    public boolean isValid(String registration, ConstraintValidatorContext ctx) {
        if (registration == null) return true;
        return !carRepository.existsByRegistrationNumber(registration);
    }


}
