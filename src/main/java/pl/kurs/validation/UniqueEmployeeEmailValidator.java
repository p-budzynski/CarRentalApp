package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.EmployeeRepository;

@RequiredArgsConstructor
public class UniqueEmployeeEmailValidator implements ConstraintValidator<UniqueEmployeeEmail, String> {
    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext ctx) {
        if (email == null || email.isBlank()) return true;
        return !employeeRepository.existsByEmail(email);
    }

}
