package pl.kurs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCustomerPhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCustomerPhone {

    String message() default "Customer phone number already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
