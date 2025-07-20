package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class YearInRangeValidatorTest {
    private final YearInRangeValidator validator = new YearInRangeValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void shouldReturnFalseWhenYearIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertFalse(result);
    }

    @Test
    void shouldReturnFalseForYear1899() {
        //when
        boolean result = validator.isValid(1899, context);

        //then
        assertFalse(result);
    }

    @Test
    void shouldReturnTrueForRecentValidYear() {
        //when
        boolean result = validator.isValid(2020, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseForFutureYear() {
        //when
        boolean result = validator.isValid(Year.now().getValue() + 1, context);

        //then
        assertFalse(result);
    }

}