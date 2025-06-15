package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class YearInRangeValidator implements ConstraintValidator<YearInRange, Integer> {
    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext context) {
        if (year == null) return false;
        int currentYear = Year.now().getValue();
        return year >= 1900 && year <= currentYear;
    }
}
