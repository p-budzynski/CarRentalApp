package pl.kurs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmployeeEmailValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmployeeEmail {

    String message() default "Employee e-mail already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
