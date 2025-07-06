package pl.kurs.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.kurs.dto.ReservationDto;

import java.time.LocalDate;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, ReservationDto> {

    @Override
    public boolean isValid(ReservationDto value, ConstraintValidatorContext context) {
        if (value == null) return true;

            LocalDate start = value.getStartDate();
            LocalDate end = value.getEndDate();

            if (start == null || end == null) return true;

            return !end.isBefore(start);
    }
}
