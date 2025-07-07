package pl.kurs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCustomerEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCustomerEmail {

    String message() default "Customer e-mail already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
