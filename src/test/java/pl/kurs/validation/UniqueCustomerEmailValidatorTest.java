package pl.kurs.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.repository.CustomerRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueCustomerEmailValidatorTest {

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private CustomerRepository customerRepository;

    private UniqueCustomerEmailValidator validator;

    @BeforeEach
    void setUp() {
    validator = new UniqueCustomerEmailValidator(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenEmailIsNull() {
        //when
        boolean result = validator.isValid(null, context);

        //then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenEmailIsEmpty() {
        //when
        boolean result = validator.isValid("", context);

        // then
        assertTrue(result);
        verifyNoInteractions(customerRepository);
    }

    @Test
    void shouldReturnTrueWhenEmailDoesNotExist() {
        //given
        String email = "user@example.com";
        when(customerRepository.existsByEmail(email)).thenReturn(false);

        //when
        boolean result = validator.isValid(email, context);

        //then
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenEmailAlreadyExists() {
        //given
        String email = "user@example.com";
        when(customerRepository.existsByEmail(email)).thenReturn(true);

        //when
        boolean result = validator.isValid(email, context);

        //then
        assertFalse(result);
    }

}