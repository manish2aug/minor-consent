package tum.ret.rity.minor.consent.infrastructure.annotation;

import tum.ret.rity.minor.consent.infrastructure.validator.ValidDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDateValidator.class)
@Documented
public @interface ValidDate {
    String message() default "Invalid date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean futureOrPresent() default false;

    boolean past() default false;

    String pattern() default "uuuu-MM-dd";

}