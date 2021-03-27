package tum.ret.rity.minor.consent.infrastructure.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import tum.ret.rity.minor.consent.infrastructure.annotation.ValidIso3166Alpha2CountryCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

public class ValidIso3166Alpha2CountryCodeValidator implements ConstraintValidator<ValidIso3166Alpha2CountryCode, String> {

    @Override
    public void initialize(ValidIso3166Alpha2CountryCode validCurrencyCode) {
        // empty block
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(value) && ArrayUtils.contains(Locale.getISOCountries(), value);
    }
}