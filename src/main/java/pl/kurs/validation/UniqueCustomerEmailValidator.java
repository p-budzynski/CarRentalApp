package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.CustomerRepository;

@RequiredArgsConstructor
public class UniqueCustomerEmailValidator implements ConstraintValidator<UniqueCustomerEmail, String> {
    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext ctx) {
        if (email == null || email.isBlank()) return true;
        return !customerRepository.existsByEmail(email);
    }

}
