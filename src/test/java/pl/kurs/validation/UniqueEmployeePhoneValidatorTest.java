package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.repository.EmployeeRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueEmployeePhoneValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private EmployeeRepository employeeRepository;

    private UniqueEmployeePhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueEmployeePhoneValidator(employeeRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberIsEmpty() {
        //when
        boolean result = validator.isValid("", context);

        //then
        assertTrue(result);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberDoesNotExist() {
        //given
        String phoneNumber = "500600700";
        when(employeeRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        //when
        boolean result = validator.isValid(phoneNumber, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPhoneNumberAlreadyExists() {
        //given
        String phoneNumber = "500600700";
        when(employeeRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        //when
        boolean result = validator.isValid(phoneNumber, context);

        //then
        assertFalse(result);
    }

}