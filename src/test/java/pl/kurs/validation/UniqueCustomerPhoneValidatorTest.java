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
class UniqueCustomerPhoneValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CustomerRepository customerRepository;

    private UniqueCustomerPhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UniqueCustomerPhoneValidator(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberIsEmpty() {
        //when
        boolean result = validator.isValid("", context);

        //then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenPhoneNumberDoesNotExist() {
        //given
        String phoneNumber = "500600700";
        when(customerRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);

        //when
        boolean result = validator.isValid(phoneNumber, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPhoneNumberAlreadyExists() {
        //given
        String phoneNumber = "500600700";
        when(customerRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        //when
        boolean result = validator.isValid(phoneNumber, context);

        //then
        assertFalse(result);
    }
}