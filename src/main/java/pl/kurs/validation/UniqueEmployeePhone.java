package pl.kurs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmployeePhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmployeePhone {

    String message() default "Employee phone number already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
