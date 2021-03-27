package tum.ret.rity.minor.consent.infrastructure.annotation;

import tum.ret.rity.minor.consent.infrastructure.validator.ValidIso3166Alpha2CountryCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidIso3166Alpha2CountryCodeValidator.class)
@Documented
public @interface ValidIso3166Alpha2CountryCode {
    String message() default "wrong iso code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}