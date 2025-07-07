package pl.kurs.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueDrivingLicenseNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueDrivingLicenseNumber {

    String message() default "Driving License number already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
