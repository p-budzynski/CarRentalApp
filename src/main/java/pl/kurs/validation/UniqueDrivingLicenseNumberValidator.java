package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.CustomerRepository;

@RequiredArgsConstructor
public class UniqueDrivingLicenseNumberValidator implements ConstraintValidator<UniqueDrivingLicenseNumber, String> {
    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(String drivingLicenseNumber, ConstraintValidatorContext ctx) {
        if (drivingLicenseNumber == null || drivingLicenseNumber.isBlank()) return true;
        return !customerRepository.existsByDrivingLicenseNumber(drivingLicenseNumber);
    }
}
