package tum.ret.rity.minor.consent.rest.representation;

import lombok.*;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;
import tum.ret.rity.minor.consent.infrastructure.annotation.ValidIso3166Alpha2CountryCode;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.logging.Level;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Log
public class UserWriteRepresentation {

    @NotNull(message = ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG)
    @Pattern(regexp = ApplicationConstants.VALID_IDENTIFIER_TYPE_REGEX,
            message = ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG)
    @JsonbProperty("identifier_type")
    private String identifierType;
    @NotBlank(message = ApplicationConstants.IDENTIFIER_VALUE_VALIDATION_ERROR_MSG)
    @Size(max = 50, message = ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG)
    @JsonbProperty("identifier_value")
    private String identifierValue;
    @ValidIso3166Alpha2CountryCode(message = ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG)
    @JsonbProperty("identifier_issuing_country")
    private String countryOfIssue;

    public void validateUserAttributes(String referenceNumber, Collection<String> validationErrors) {
        if (StringUtils.equals(identifierType, IdentifierTypeEnum.ID.name()))
            validateRepresentationForID(referenceNumber, validationErrors);
    }

    private void validateRepresentationForID(String referenceNumber, Collection<String> validationErrors) {

        if (!StringUtils.equals(countryOfIssue, ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE)) {
            log.severe(() -> String.format("%s| Invalid country (%s) supplied for identifier type as ID", referenceNumber, countryOfIssue));
            validationErrors.add(ApplicationConstants.INVALID_COUNTRY_FOR_ID);
        }

        if (!ApplicationConstants.isValidIdNumber(identifierValue)) {
            log.log(Level.SEVERE,
                    String.format("%s| ID number validation failure [identifier_issuing_country: %s, identifier_type: %s, identifier_value: %s]",
                            referenceNumber,
                            countryOfIssue,
                            identifierType,
                            identifierValue));
            validationErrors.add(ApplicationConstants.ID_VALIDATION_ERROR_MSG);
        }
    }

    public User getDomainObject() {
        return User.builder()
                .countryOfIssue(countryOfIssue)
                .identifierType(IdentifierTypeEnum.fromString(identifierType))
                .identifierValue(identifierValue)
                .build();
    }

    public boolean hasValidIdNumber() {
        return StringUtils.equals(identifierType, IdentifierTypeEnum.ID.name())
                && ApplicationConstants.isValidIdNumber(identifierValue);
    }

    public String getIdNumber() {
        return (StringUtils.equals(identifierType, IdentifierTypeEnum.ID.name())) ? identifierValue : null;
    }

    public String getPassportNumber() {
        return (StringUtils.equals(identifierType, IdentifierTypeEnum.PASSPORT.name())) ? identifierValue : null;
    }

    public boolean isMinor() {
        return hasValidIdNumber() && ApplicationConstants.isMinorAsPerIdNumber(identifierValue);
    }
}