package tum.ret.rity.minor.consent.rest.representation;

import lombok.*;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.infrastructure.annotation.ValidDate;
import tum.ret.rity.minor.consent.infrastructure.exception.BadRequestException;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Log
@EqualsAndHashCode
public class ConsentWriteRepresentation {

    @JsonbProperty("consent_applicable_date")
    @ValidDate(futureOrPresent = true, message = ApplicationConstants.CONSENT_APPLICABLE_DATE_VALIDATION_ERROR_MSG)
    private String consentApplicableDate;
    @NotNull(message = "guardian is mandatory")
    @Valid
    private UserWriteRepresentation guardian;
    @NotNull(message = "minor is mandatory")
    @Valid
    private UserWriteRepresentation minor;
    @ValidDate(past = true, message = ApplicationConstants.MINOR_BIRTH_DATE_VALIDATION_ERROR_MSG_2)
    @JsonbProperty("birth_date")
    private String birthDate;
    @JsonbProperty("originating_system")
    private String originatingSystem;
    @JsonbTransient
    private String clientAppNameClaim;
    @JsonbTransient
    private String subjectClaim;

    public ConsentWriteRepresentation(LocalDate consentApplicableDate, UserWriteRepresentation guardian, UserWriteRepresentation minor, LocalDate birthDate, String originatingSystem) {
        this.consentApplicableDate = (consentApplicableDate != null) ? consentApplicableDate.toString() : null;
        this.guardian = guardian;
        this.minor = minor;
        this.birthDate = (birthDate != null) ? birthDate.toString() : null;
        this.originatingSystem = originatingSystem;
    }

    @JsonbTransient
    @SneakyThrows
    public Consent getDomainObject(String referenceNumber) {
        setDefaults(referenceNumber);
        validate(referenceNumber);

        return Consent.builder()
                .consentApplicableDate(LocalDate.parse(consentApplicableDate))
                .guardian(guardian.getDomainObject())
                .minor(minor.getDomainObject())
                .birthDate(LocalDate.parse(birthDate))
                .originatingSystem(originatingSystem)
                .build();
    }

    private void validate(String referenceNumber) throws BadRequestException {
        log.log(Level.INFO, String.format("%s| Validating representation", referenceNumber));

        Collection<String> validationErrors = getValidationErrors(referenceNumber);
        if (!validationErrors.isEmpty()) {
            throw new BadRequestException(validationErrors, referenceNumber);
        }
    }

    @SneakyThrows
    @JsonbTransient
    public Collection<String> getValidationErrors(String referenceNumber) {
        Collection<String> validationErrors = new ArrayList<>();

        validateGuardianAttributes(referenceNumber, validationErrors);
        validateMinorAttributes(referenceNumber, validationErrors);

        return validationErrors;
    }

    private void validateMinorAttributes(
            String referenceNumber,
            Collection<String> validationErrors) {

        minor.validateUserAttributes(referenceNumber, validationErrors);
        validateConsentAttributesForMinor(referenceNumber, validationErrors);
    }

    private void validateConsentAttributesForMinor(
            String referenceNumber,
            Collection<String> validationErrors) {

        if (ApplicationConstants.getLocalDate(birthDate) == null) {
            log.severe(() -> String.format("%s| Effective birthDate (%s) of minor is invalid", referenceNumber, birthDate));
            validationErrors.add(ApplicationConstants.MINOR_EFFECTIVE_BIRTH_DATE_VALIDATION_ERROR_MSG);
        } else if (!ApplicationConstants.isMinor(birthDate)) {
            log.severe(() -> String.format("%s| As per effective birthDate (%s) of minor, age criteria doesn't match", referenceNumber, birthDate));
            validationErrors.add(ApplicationConstants.MINOR_EFFECTIVE_BIRTH_DATE_VALIDATION_ERROR_MSG_2);
        } else {
            String idNumber = minor.getIdNumber();
            if (hasUserValidIdNumber(minor) && !ApplicationConstants.isValidBirthDateAsPerID(idNumber, birthDate)) {
                log.severe(() -> String.format("%s| Supplied birthDate (%s) is not in compliance with supplied minor's ID (%s)", referenceNumber, birthDate, idNumber));
                validationErrors.add(ApplicationConstants.MINOR_EFFECTIVE_BIRTH_DATE_INVALID);
            }
        }
    }

    private boolean hasUserValidIdNumber(UserWriteRepresentation representation) {
        return StringUtils.equals(representation.getIdentifierType(), IdentifierTypeEnum.ID.name())
                && ApplicationConstants.isValidIdNumber(representation.getIdentifierValue());
    }

    private void validateGuardianAttributes(String referenceNumber, Collection<String> validationErrors) {
        guardian.validateUserAttributes(referenceNumber, validationErrors);
        validateConsentAttributesForGuardian(referenceNumber, validationErrors);
    }

    private void validateConsentAttributesForGuardian(String referenceNumber, Collection<String> validationErrors) {
        if (guardian.isMinor()) {
            log.severe(() -> String.format("%s| As per the ID number (%s) of guardian, age criteria doesn't match", referenceNumber, guardian.getIdentifierValue()));
            validationErrors.add(ApplicationConstants.GUARDIAN_ID_VALIDATION_ERROR_MSG_2);
        }
    }

    private void setDefaults(String referenceNumber) {
        deriveOriginatingSystemIfNotProvided(referenceNumber);
        deriveMinorBirthDateFromIdNumberIfNotProvided(referenceNumber);
        deriveConsentApplicableDateIfNotProvided(referenceNumber);

    }

    private void deriveConsentApplicableDateIfNotProvided(String referenceNumber) {
        if (StringUtils.isBlank(consentApplicableDate)) {
            log.warning(() -> String.format("%s| Using current date (%s) as the consent_applicable_date in absence of supplied value", referenceNumber, LocalDate.now().toString()));
            consentApplicableDate = LocalDate.now().toString();
        }
    }

    private void deriveMinorBirthDateFromIdNumberIfNotProvided(String referenceNumber) {
        if (isBirthDateNotProvidedButHasValidSouthAfricanIdNumber()) {
            String minorIdNumber = minor.getIdentifierValue();
            LocalDate dateOfBirthFromID = ApplicationConstants.getDateOfBirthFromID(minorIdNumber);
            if (dateOfBirthFromID != null) {
                String dateOfBirthByIdNumber = dateOfBirthFromID.toString();
                log.log(Level.WARNING,
                        String.format("%s| Overriding minor's supplied birth_date (%s) by the one (%s), derived from ID number (%s)",
                                referenceNumber,
                                birthDate,
                                dateOfBirthByIdNumber,
                                minorIdNumber));
                this.birthDate = dateOfBirthByIdNumber;
            }
        }
    }

    private boolean isBirthDateNotProvidedButHasValidSouthAfricanIdNumber() {
        return StringUtils.isBlank(birthDate)
                && StringUtils.equals(minor.getIdentifierType(), IdentifierTypeEnum.ID.name())
                && StringUtils.equals(minor.getCountryOfIssue(), "ZA")
                && ApplicationConstants.isValidIdNumber(minor.getIdentifierValue());
    }

    private void deriveOriginatingSystemIfNotProvided(String referenceNumber) {
        if (StringUtils.isBlank(originatingSystem)) {
            log.log(Level.WARNING,
                    String.format("%s| Overriding originating_system value by subject (%s) azp (%s) claims in JWT token",
                            referenceNumber,
                            subjectClaim,
                            clientAppNameClaim));
            originatingSystem = String.join("|", subjectClaim, clientAppNameClaim);
        }
    }
}
