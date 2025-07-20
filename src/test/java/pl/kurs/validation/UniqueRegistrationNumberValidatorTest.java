package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.repository.CarRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueRegistrationNumberValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CarRepository carRepository;

    private UniqueRegistrationNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueRegistrationNumberValidator(carRepository);
    }

    @Test
    void shouldReturnTrueWhenRegistrationNumberIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
        verifyNoInteractions(carRepository);
    }

    @Test
    void shouldReturnTrueWhenRegistrationNumberIsEmpty() {
        //when
        boolean result = validator.isValid("", context);

        //then
        assertTrue(result);
        verifyNoInteractions(carRepository);
    }

    @Test
    void shouldReturnTrueWhenRegistrationNumberDoesNotExist() {
        //given
        String registrationNumber = "WA 12345";
        when(carRepository.existsByRegistrationNumber(registrationNumber)).thenReturn(false);

        //when
        boolean result = validator.isValid(registrationNumber, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenRegistrationNumberAlreadyExists() {
        //given
        String registrationNumber = "WA 12345";
        when(carRepository.existsByRegistrationNumber(registrationNumber)).thenReturn(true);

        //when
        boolean result = validator.isValid(registrationNumber, context);

        //then
        assertFalse(result);
    }

}