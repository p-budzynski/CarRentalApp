package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.repository.CustomerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueDrivingLicenseNumberValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CustomerRepository customerRepository;

    private UniqueDrivingLicenseNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueDrivingLicenseNumberValidator(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenDrivingLicenseNumberIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenDrivingLicenseNumberIsEmpty() {
        //when
        boolean result = validator.isValid("", context);

        //then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenDrivingLicenseNumberDoesNotExist() {
        //given
        String drivingLicenseNumber = "WXX123456";
        when(customerRepository.existsByDrivingLicenseNumber(drivingLicenseNumber)).thenReturn(false);

        //when
        boolean result = validator.isValid(drivingLicenseNumber, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenDrivingLicenseNumberAlreadyExists() {
        //given
        String drivingLicenseNumber = "WXX123456";
        when(customerRepository.existsByDrivingLicenseNumber(drivingLicenseNumber)).thenReturn(true);

        //when
        boolean result = validator.isValid(drivingLicenseNumber, context);

        //then
        assertFalse(result);
    }
}