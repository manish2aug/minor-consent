package tum.ret.rity.minor.consent.rest.resource;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.infrastructure.annotation.ScopesAllowed;
import tum.ret.rity.minor.consent.infrastructure.annotation.ValidIso3166Alpha2CountryCode;
import tum.ret.rity.minor.consent.rest.representation.ConsentWriteRepresentation;
import tum.ret.rity.minor.consent.service.ConsentApplicationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

/**
 * @author manish2aug
 */
@Path("/consents")
@Log
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ConsentRestResource {

    @HeaderParam(ApplicationConstants.REQUEST_ID)
    String referenceNumber;
    @Context
    HttpHeaders headers;
    @Inject
    private ConsentApplicationService applicationService;

    @SneakyThrows
    @GET
    @ScopesAllowed({"consent", "consent-admin"})
    public Response getConsent(
            @QueryParam("identifier_type") @Pattern(regexp = ApplicationConstants.VALID_IDENTIFIER_TYPE_REGEX, message = ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG) String minorIdentifierType,
            @QueryParam("identifier_value") @Size(min = 1, max = 50, message = ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG) String minorIdentifierValue,
            @QueryParam("identifier_issuing_country") @ValidIso3166Alpha2CountryCode(message = ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG) String minorIssuingCountry) {

        log.info(() -> String.format(
                "%s| Consent retrieval requested for identifier_type: %s, identifier_value: %s, identifier_issuing_country: %s",
                referenceNumber, minorIdentifierType, minorIdentifierValue, minorIssuingCountry));

        return Response
                .ok()
                .entity(applicationService.getConsent(minorIdentifierType, minorIdentifierValue, minorIssuingCountry))
                .build();
    }

    @SneakyThrows
    @GET
    @Path("reports")
    @ScopesAllowed({"consent-admin"})
    public Response getAllConsents(
            @QueryParam("from_date") @Pattern(regexp = ApplicationConstants.VALID_DATE_REGEX, message = ApplicationConstants.INVALID_FROM_DATE_ERROR_MSG) String fromDate,
            @QueryParam("to_date") @Pattern(regexp = ApplicationConstants.VALID_DATE_REGEX, message = ApplicationConstants.INVALID_TO_DATE_ERROR_MSG) String toDate,
            @QueryParam("include_withdrawn") @Pattern(regexp = "true|false", flags = Pattern.Flag.CASE_INSENSITIVE, message = ApplicationConstants.INVALID_REPORT_FLAG_ERROR_MSG) String includeWithdrawn) {

        log.info(() -> String.format(
                "%s| Consent report retrieval requested for from_date: %s, to_date: %s, include_withdrawn: %s",
                referenceNumber, fromDate, toDate, includeWithdrawn));

        return Response
                .ok()
                .entity(applicationService.getAllConsent(fromDate, toDate, includeWithdrawn))
                .build();
    }

    @SneakyThrows
    @DELETE
    @ScopesAllowed({"consent", "consent-admin"})
    public Response revokeConsent(
            @QueryParam("identifier_type") @Pattern(regexp = ApplicationConstants.VALID_IDENTIFIER_TYPE_REGEX, message = ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG) String identifierType,
            @QueryParam("identifier_value") @Size(min = 1, max = 50, message = ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG) String identifierValue,
            @QueryParam("identifier_issuing_country") @ValidIso3166Alpha2CountryCode(message = ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG) String issuingCountry) {

        log.info(() -> String.format(
                "%s| Consent revoke requested for identifier_type: %s, identifier_value: %s, identifier_issuing_country: %s",
                referenceNumber, identifierType, identifierValue, issuingCountry));

        applicationService.revokeConsent(identifierType, identifierValue, issuingCountry);

        return Response
                .noContent()
                .build();
    }

    @SneakyThrows
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ScopesAllowed({"consent", "consent-admin"})
    public Response addConsent(
            @Context UriInfo uriInfo,
            @Valid ConsentWriteRepresentation writeRepresentation) {

        log.info(() -> String.format("%s| New consent creation requested, payload: %s", referenceNumber, writeRepresentation));
        applicationService.addConsent(writeRepresentation);

        URI uri = ApplicationConstants.rewriteUrlSafely(
                uriInfo.getAbsolutePathBuilder()
                        .queryParam("identifier_type", writeRepresentation.getMinor().getIdentifierType())
                        .queryParam("identifier_value", writeRepresentation.getMinor().getIdentifierValue())
                        .queryParam("identifier_issuing_country", writeRepresentation.getMinor().getCountryOfIssue())
                        .build()
                        .toString());

        return Response
                .created(uri)
                .build();
    }
}
