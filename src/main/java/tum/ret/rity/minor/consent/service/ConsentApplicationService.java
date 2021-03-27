package tum.ret.rity.minor.consent.service;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.Claim;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;
import tum.ret.rity.minor.consent.infrastructure.exception.*;
import tum.ret.rity.minor.consent.repository.ConsentRepository;
import tum.ret.rity.minor.consent.rest.representation.ConsentReadRepresentation;
import tum.ret.rity.minor.consent.rest.representation.ConsentWriteRepresentation;
import tum.ret.rity.minor.consent.rest.representation.UserWriteRepresentation;

import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import java.time.LocalDate;
import java.util.Collection;
import java.util.logging.Level;

@Log
@Setter
public class ConsentApplicationService {

    @HeaderParam(ApplicationConstants.REQUEST_ID)
    String referenceNumber;
    @Inject
    @Claim("azp")
    private String clientAppClaim;
    @Inject
    @Claim("dateOfBirth")
    private String consentGranterBirthDateClaim;
    @Inject
    @Claim("preferred_username")
    private String consentGranterUsernameClaim;
    @Inject
    @Claim("idNumber")
    private String consentGranterIdNumberClaim;
    @Inject
    @Claim("passportNumber")
    private String consentGranterPassportNumberClaim;
    @Inject
    @Claim("passportCountryOfIssue")
    private String consentGranterPassportCountryOfIssueClaim;
    @Inject
    private ConsentRepository consentRepository;
    @Inject
    @Claim("scope")
    private String scopeClaim;
    @Inject
    @Claim("sub")
    private String subjectClaim;

    @SneakyThrows({ConsentAlreadyExistException.class, UnrecoverableException.class, BadRequestException.class})
    public void addConsent(ConsentWriteRepresentation writeRepresentation) {

        if (isNotConsentAdmin())
            validateApiCaller(referenceNumber, writeRepresentation.getGuardian());

        writeRepresentation.setClientAppNameClaim(clientAppClaim);
        writeRepresentation.setSubjectClaim(subjectClaim);

        consentRepository
                .addMinorConsent(
                        writeRepresentation.getDomainObject(
                                referenceNumber));
    }

    private boolean isNotConsentAdmin() {
        return !StringUtils.contains(scopeClaim, "consent-admin");
    }

    private void validateApiCaller(String referenceNumber, UserWriteRepresentation representation) throws BadRequestException {
        log.log(Level.INFO, String.format("%s| Validating caller permission", referenceNumber));

        if (StringUtils.isNotBlank(consentGranterIdNumberClaim)) {
            String guardianIdNumber = representation.getIdNumber();
            String logMsg = null;
            if (!ApplicationConstants.isValidIdNumber(consentGranterIdNumberClaim))
                logMsg = String.format("%s| ID (%s) in token, is not valid", referenceNumber, consentGranterIdNumberClaim);
            else if (ApplicationConstants.isMinorAsPerIdNumber(consentGranterIdNumberClaim))
                logMsg = String.format("%s| ID (%s) in token, doesn't match the age criteria (>18)", referenceNumber, consentGranterIdNumberClaim);
            else if (!StringUtils.equals(guardianIdNumber, consentGranterIdNumberClaim))
                logMsg = String.format("%s| Guardian's ID (%s) in payload does not match with the one (%s) in token", referenceNumber, guardianIdNumber, consentGranterIdNumberClaim);

            if (logMsg != null) {
                log.log(Level.SEVERE, logMsg);
                throw new BadRequestException(ApplicationConstants.DATA_INTEGRITY_FAILURE, referenceNumber);
            }
        }
        if (StringUtils.isNotBlank(consentGranterPassportNumberClaim) && StringUtils.isBlank(consentGranterIdNumberClaim)) {
            String logMsg = null;
            String guardianPassportNumber = representation.getPassportNumber();
            String countryOfIssue = representation.getCountryOfIssue();

            if (StringUtils.isBlank(consentGranterBirthDateClaim))
                logMsg = String.format("%s| Unable to verify the caller age criteria, values in token [ID: %s, birthDate: %s]", referenceNumber, consentGranterIdNumberClaim, consentGranterBirthDateClaim);
            else if (ApplicationConstants.isMinor(consentGranterBirthDateClaim))
                logMsg = String.format("%s| dateOfBirth (%s) in token, doesn't match the age criteria (>18)", referenceNumber, consentGranterBirthDateClaim);
            else if (!StringUtils.equals(guardianPassportNumber, consentGranterPassportNumberClaim))
                logMsg = String.format("%s| Guardian's passport number (%s) does not match with the one (%s) in token", referenceNumber, guardianPassportNumber, consentGranterPassportNumberClaim);
            else if (StringUtils.isNotBlank(consentGranterPassportCountryOfIssueClaim) && !StringUtils.equals(countryOfIssue, consentGranterPassportCountryOfIssueClaim))
                logMsg = String.format("%s| Guardian's passport country of issue (%s) does not match with the one (%s) in token", referenceNumber, countryOfIssue, consentGranterPassportCountryOfIssueClaim);

            if (logMsg != null) {
                log.log(Level.SEVERE, logMsg);
                throw new BadRequestException(ApplicationConstants.DATA_INTEGRITY_FAILURE, referenceNumber);
            }
        }

    }

    public ConsentReadRepresentation getConsent(
            String minorIdentifierType,
            String minorIdentifierValue,
            String minorIssuingCountry) throws BadRequestException, NoConsentFoundException, ForbiddenException {

        Consent consent = validateInputCheckPermissionAndRetrieve(minorIdentifierType, minorIdentifierValue, minorIssuingCountry);
        return ConsentReadRepresentation.getReadRepresentation(consent);
    }

    public void revokeConsent(
            String minorIdentifierType,
            String minorIdentifierValue,
            String minorIssuingCountry) throws NoConsentFoundException, BadRequestException, ForbiddenException {

        validateInputCheckPermissionAndRetrieve(minorIdentifierType, minorIdentifierValue, minorIssuingCountry);
        consentRepository.revokeConsent(minorIdentifierType, minorIdentifierValue, minorIssuingCountry);
    }

    private Consent validateInputCheckPermissionAndRetrieve(
            String minorIdentifierType,
            String minorIdentifierValue,
            String minorIssuingCountry) throws BadRequestException, NoConsentFoundException, ForbiddenException {

        validateInputForLookup(minorIdentifierType, minorIdentifierValue, minorIssuingCountry);

        Consent consent = consentRepository.getConsent(minorIdentifierType, minorIdentifierValue, minorIssuingCountry);
        if (StringUtils.contains(scopeClaim, "consent-admin")) {
            log.log(Level.INFO, String.format("%s| Permission check bypassed for admin", referenceNumber));
            return consent;
        }

        User guardian = consent.getGuardian();
        IdentifierTypeEnum consentGranterIdentifierType = guardian.getIdentifierType();
        String consentGranterIdentifierValue = guardian.getIdentifierValue();

        if (isRequesterDifferentFromGranter(consentGranterIdentifierType, consentGranterIdentifierValue, IdentifierTypeEnum.ID, consentGranterIdNumberClaim) ||
                isRequesterDifferentFromGranter(consentGranterIdentifierType, consentGranterIdentifierValue, IdentifierTypeEnum.PASSPORT, consentGranterPassportNumberClaim)
        ) {

            log.severe(() -> String.format(
                    "%s| The guardian (identifier_type=%s, identifier_value=%s) who granted the consent, can revoke the consent but requester is different (ID=%s, Passport=%s)",
                    referenceNumber,
                    consentGranterIdentifierType,
                    consentGranterIdentifierValue,
                    consentGranterIdNumberClaim,
                    consentGranterPassportNumberClaim));
            throw new ForbiddenException(ApplicationConstants.NOT_AUTHORIZED_ERROR_MSG, referenceNumber);
        }
        log.log(Level.INFO, String.format("%s| Permission check successful", referenceNumber));
        return consent;
    }

    private boolean isRequesterDifferentFromGranter(
            IdentifierTypeEnum consentGranterIdentifierType,
            String consentGranterIdentifierValue,
            IdentifierTypeEnum identifierType,
            String requesterIdentifierValue) {

        return consentGranterIdentifierType == identifierType
                && !StringUtils.equals(consentGranterIdentifierValue, requesterIdentifierValue);
    }

    private void validateInputForLookup(String identifierType, String identifierValue, String issuingCountry) throws BadRequestException {
        if (StringUtils.isAnyBlank(identifierType, identifierValue, issuingCountry)) {
            String msg = "%s| Invalid lookup criteria [issuingCountry: %s, identifierType: %s, identifierValue: %s]";
            log.severe(() -> String.format(msg, referenceNumber, issuingCountry, identifierType, identifierValue));
            throw new BadRequestException(ApplicationConstants.INVALID_SEARCH_CRITERIA_ERROR_MSG, referenceNumber);
        } else if (StringUtils.equals(identifierType, IdentifierTypeEnum.ID.name())
                && !StringUtils.equals(issuingCountry, ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE)) {
            log.severe(() -> String.format("%s| Invalid country (%s) supplied for identifier type as ID",
                    referenceNumber, issuingCountry));
            throw new BadRequestException(ApplicationConstants.INVALID_COUNTRY_FOR_ID, referenceNumber);
        } else if (StringUtils.equals(issuingCountry, ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE)
                && StringUtils.equals(identifierType, IdentifierTypeEnum.ID.name())
                && !ApplicationConstants.isValidIdNumber(identifierValue)) {

            String msg = "%s| ID number validation failure [issuingCountry: %s, identifierType: %s, identifierValue: %s]";
            log.severe(() -> String.format(msg, referenceNumber, issuingCountry, identifierType, identifierValue));
            throw new BadRequestException(ApplicationConstants.MINOR_ID_NUMBER_VALIDATION_ERROR_MSG, referenceNumber);
        }
    }

    public Collection<ConsentReadRepresentation> getAllConsent(
            String fromDate,
            String toDate,
            String includeWithdrawn) throws BadRequestException {

        if (StringUtils.isAnyBlank(fromDate, toDate)) {
            log.severe(() -> String.format("%s| Invalid report request [fromDate: %s, toDate: %s]",
                    referenceNumber, fromDate, toDate));
            throw new BadRequestException(ApplicationConstants.INVALID_REPORT_CRITERIA_ERROR_MSG, referenceNumber);
        }

        LocalDate localDateFrom = LocalDate.parse(fromDate);
        LocalDate localDateTo = LocalDate.parse(toDate);
        if (localDateFrom.isAfter(localDateTo)) {
            log.severe(() -> String.format("%s| Date range validation failure [from_date: %s, to_date: %s, include_withdrawn: %s]",
                    referenceNumber, localDateFrom, localDateTo, includeWithdrawn));
            throw new BadRequestException(ApplicationConstants.INVALID_DATE_RANGE_CRITERIA_ERROR_MSG, referenceNumber);
        }

        return ConsentReadRepresentation.getReadRepresentation(
                consentRepository.getAllConsent(
                        localDateFrom,
                        localDateTo,
                        Boolean.parseBoolean(includeWithdrawn)
                ));
    }


}
