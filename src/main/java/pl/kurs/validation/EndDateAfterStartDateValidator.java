package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            var clazz = value.getClass();
            var startField = clazz.getDeclaredField("startDate");
            var endField = clazz.getDeclaredField("endDate");
            startField.setAccessible(true);
            endField.setAccessible(true);

            LocalDate start = (LocalDate) startField.get(value);
            LocalDate end = (LocalDate) endField.get(value);

            if (start == null || end == null) return true;

            return !end.isBefore(start);

        } catch (Exception e) {
            return false;
        }
    }
}
