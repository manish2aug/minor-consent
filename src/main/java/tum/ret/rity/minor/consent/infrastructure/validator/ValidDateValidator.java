package tum.ret.rity.minor.consent.infrastructure.validator;

import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.infrastructure.annotation.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ValidDateValidator implements ConstraintValidator<ValidDate, String> {

    private boolean futureOrPresent;
    private boolean past;
    private String pattern;

    @Override
    public void initialize(ValidDate validDate) {
        this.futureOrPresent = validDate.futureOrPresent();
        this.past = validDate.past();
        this.pattern = validDate.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return value == null
                || (ApplicationConstants.getLocalDate(value, formatter) != null
                && (!futureOrPresent || !LocalDate.parse(value, formatter).isBefore(LocalDate.now()))
                && (!past || LocalDate.parse(value, formatter).isBefore(LocalDate.now())));
    }
}