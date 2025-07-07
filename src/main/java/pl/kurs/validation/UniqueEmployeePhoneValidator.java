package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.EmployeeRepository;

@RequiredArgsConstructor
public class UniqueEmployeePhoneValidator implements ConstraintValidator<UniqueEmployeePhone, String> {
    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext ctx) {
        if (phoneNumber == null || phoneNumber.isBlank()) return true;
        return !employeeRepository.existsByPhoneNumber(phoneNumber);
    }
}
