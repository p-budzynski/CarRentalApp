package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.dto.ReservationDto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EndDateAfterStartDateValidatorTest {
    private final EndDateAfterStartDateValidator validator = new EndDateAfterStartDateValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Test
    void shouldReturnTrueWhenReservationDtoIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenStartDateIsNull() {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L,
                null, LocalDate.of(2024, 1, 15), new BigDecimal(200), "RESERVED");

        //when
        boolean result = validator.isValid(reservationDto, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenEndDateIsNull() {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L,
                LocalDate.of(2024, 1, 15), null, new BigDecimal(200), "RESERVED");

        //when
        boolean result = validator.isValid(reservationDto, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnTrueWhenEndDateIsAfterStartDate() {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L, LocalDate.of(2024, 1, 10),
                LocalDate.of(2024, 1, 15), new BigDecimal(200), "RESERVED");

        //when
        boolean result = validator.isValid(reservationDto, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEndDateIsBeforeStartDate() {
        //given
        ReservationDto reservationDto = new ReservationDto(1L, 1L, LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 10), new BigDecimal(200), "RESERVED");

        //when
        boolean result = validator.isValid(reservationDto, context);

        //then
        assertFalse(result);
    }


}