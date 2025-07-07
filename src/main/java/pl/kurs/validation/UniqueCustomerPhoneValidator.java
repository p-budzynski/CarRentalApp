package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import pl.kurs.repository.CustomerRepository;

@RequiredArgsConstructor
public class UniqueCustomerPhoneValidator implements ConstraintValidator<UniqueCustomerPhone, String> {
    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext ctx) {
        if (phoneNumber == null || phoneNumber.isBlank()) return true;
        return !customerRepository.existsByPhoneNumber(phoneNumber);
    }
}
