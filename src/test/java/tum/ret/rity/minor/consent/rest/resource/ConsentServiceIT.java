package tum.ret.rity.minor.consent.rest.resource;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.microshed.testing.ApplicationEnvironment;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jupiter.MicroShedTest;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import tum.ret.rity.minor.consent.AppDeploymentConfig;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This test uses the dev database and pre keyclock token
 */
@MicroShedTest
@SharedContainerConfig(AppDeploymentConfig.class)
public class ConsentServiceIT {

    public static String keycloakTokenWithConsentScope;
    public static String keycloakTokenWithConsentAdminScope;
    public static String keycloakTokenWithNoScope;
    public static String keycloakTokenForMinorWithAllScopes;
    private static String app_base_path;
    private static String keycloakTokenWithConsentScopeOtherUser;
    private final ClassLoader classLoader = getClass().getClassLoader();

    @BeforeAll
    static void beforeAll() throws Exception {
        keycloakTokenWithConsentScope = TestUtil.getKeyclockToken("consent");
        System.out.println("keycloakTokenWithConsentScope: " + keycloakTokenWithConsentScope);
        keycloakTokenWithConsentScopeOtherUser = TestUtil.getKeyclockToken("consent");
        System.out.println("keycloakTokenWithConsentScopeOtherUser: " + keycloakTokenWithConsentScopeOtherUser);
        keycloakTokenWithConsentAdminScope = TestUtil.getKeyclockToken("consent-admin");
        System.out.println("keycloakTokenWithConsentAdminScope: " + keycloakTokenWithConsentAdminScope);
        keycloakTokenWithNoScope = TestUtil.getKeyclockToken();
        System.out.println("keycloakTokenWithNoScope: " + keycloakTokenWithNoScope);
        keycloakTokenForMinorWithAllScopes = TestUtil.getKeyclockToken();
        System.out.println("keycloakTokenForMinorWithAllScopes: " + keycloakTokenForMinorWithAllScopes);
        app_base_path = StringUtils.join(ApplicationEnvironment.Resolver.load().getApplicationURL(), "/api");
    }

    /* --------------------------- Add Consent tests ---------------------------------------*/

    @Test
    public void testCreateConsent_InvalidJson() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid.json", 400
        );
    }

    @Test
    public void testCreateConsent_InvalidGuardianIdentifierType() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_invalid_guardian_identifier_type.json",
                400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_InvalidGuardianID() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_guardian_id.json", 400
        );
    }

    @Test
    public void testCreateConsent_InvalidGuardianCountry() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_guardian_identifier_country.json", 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_InvalidGuardianCountryPassport() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_guardian_identifier_country_passport.json", 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_InvalidGuardianPassportNumber() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_guardian_invalid_passport.json", 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_InvalidMinorDobInvalidDate() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_minor_dob_invalid.json", 400,
                ApplicationConstants.MINOR_BIRTH_DATE_VALIDATION_ERROR_MSG_2);
    }

    @Test
    public void testCreateConsent_InvalidMinorDobFutureDate() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_minor_dob_invalid.json", 400,
                ApplicationConstants.MINOR_BIRTH_DATE_VALIDATION_ERROR_MSG_2);
    }

    @Test
    public void testCreateConsent_InvalidMinorDobTooOld() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_minor_dob_too_old.json",
                400,
                ApplicationConstants.MINOR_EFFECTIVE_BIRTH_DATE_VALIDATION_ERROR_MSG_2);
    }

    @Test
    public void testCreateConsent_InvalidGuardianMismatchID() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_guardian_mismatch_ID.json", 400,
                ApplicationConstants.DATA_INTEGRITY_FAILURE);
    }

    @Test
    public void testCreateConsent_InvalidConsentApplicableDate() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_invalid_consent_applicable_date.json", 400,
                ApplicationConstants.CONSENT_APPLICABLE_DATE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_PastConsentApplicableDate() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_past_consent_applicable_date.json", 400,
                ApplicationConstants.CONSENT_APPLICABLE_DATE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_AllInvalid() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope, "create_consent_all_invalid.json", 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG,
                ApplicationConstants.MINOR_BIRTH_DATE_VALIDATION_ERROR_MSG_2);
    }

    @Test
    public void testCreateConsent_BlankGuardianData() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_guardian_data_blank.json",
                400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_NullGuardianData() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_guardian_data_null.json",
                400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_BlankMinorData() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_minor_data_blank.json",
                400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_NullMinorData() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_minor_data_null.json",
                400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_VALUE_VALIDATION_ERROR_MSG,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testCreateConsent_MissingRequiredScope() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithNoScope,
                "create_consent_valid_minimal_payload.json",
                403);
    }

    @Test
    public void testCreateConsent_ShouldWorkWithConsentAdminScope() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentAdminScope,
                "create_consent_valid_minimal_payload1.json",
                201);
        verifyDeleteConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                204);
    }

    @Test
    public void testCreateConsent_ByMinorGranter() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentAdminScope,
                "create_consent_valid_minimal_payload.json",
                400,
                ApplicationConstants.GUARDIAN_ID_VALIDATION_ERROR_MSG_2);
    }

    @Test
    public void testCreateConsent_WithWrongPassportCountryOfIssue() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_valid_minimal_payload_with_different_countryIssue.json",
                400,
                ApplicationConstants.DATA_INTEGRITY_FAILURE);
    }


    /* --------------------------- Get Consent tests ---------------------------------------*/

    @Test
    public void testGetConsent_UnknownIdentifierType() throws Exception {
        verifyGetConsent(
                "PASSPORTA",
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_BlankIdentifierType() throws Exception {
        verifyGetConsent(
                "",
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_NullIdentifierType() throws Exception {
        verifyGetConsent(
                null,
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.INVALID_SEARCH_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetConsent_TooLongIdentifierValue() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "J6025323232323233333333333333333333333333333333333333333333333333333333333333333333333333333",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_InvalidIdentifierValueForID() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.ID.name(),
                "1234123412341234",
                "ZA",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.MINOR_ID_NUMBER_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_BlankIdentifierValue() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_NullIdentifierValue() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                null,
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.INVALID_SEARCH_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetConsent_TooLongCountry() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "INA",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_UnknownCountry() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "AB",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_BlankCountry() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testGetConsent_NullCountry() throws Exception {
        verifyGetConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                null,
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }


    @Test
    public void testGetConsent_MissingRequiredScope() throws Exception {
        verifyGetConsent(
                "2020-01-31",
                "2020-12-31",
                "IN",
                keycloakTokenWithNoScope,
                403);
    }

    @Test
    public void testGetConsent_ShouldNotWorkInvalidCountryForId() throws Exception {
        verifyGetConsent(
                "ID",
                "asdasd",
                "IN",
                keycloakTokenWithConsentAdminScope,
                400);
    }

    @Test
    public void testGetConsent_ShouldWorkWithConsentAdminScope() throws Exception {
        verifyGetConsent(
                "ID",
                "0101017838083",
                "ZA",
                keycloakTokenWithConsentAdminScope,
                404);
    }

    @Test
    public void testGetConsent_RequesterOtherThanGuardian() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_valid_minimal_payload1.json",
                201);
        verifyGetAllConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "J6025326",
                "IN",
                keycloakTokenWithConsentScopeOtherUser,
                403,
                ApplicationConstants.NOT_AUTHORIZED_ERROR_MSG);
        verifyDeleteConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                204);
    }

    /* --------------------------- Delete Consent tests ---------------------------------------*/

    @Test
    public void testDeleteConsent_UnknownIdentifierType() throws Exception {
        verifyDeleteConsent(
                "PASSPORTA",
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_BlankIdentifierType() throws Exception {
        verifyDeleteConsent(
                "",
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_TYPE_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_NullIdentifierType() throws Exception {
        verifyDeleteConsent(
                null,
                "J6025323",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.INVALID_SEARCH_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_TooLongIdentifierValue() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "J6025323232323233333333333333333333333333333333333333333333333333333333333333333333333333333",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_InvalidIdentifierValueForID() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.ID.name(),
                "1234123412341234",
                "ZA",
                keycloakTokenWithConsentScope,
                400,
                ApplicationConstants.MINOR_ID_NUMBER_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_BlankIdentifierValue() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "",
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_NullIdentifierValue() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                null,
                "IN",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.INVALID_SEARCH_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_TooLongCountry() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "INA",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_UnknownCountry() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "AB",
                keycloakTokenWithConsentScope, 400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_BlankCountry() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                "",
                keycloakTokenWithConsentScope,
                400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_NullCountry() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "sasdfasdfas",
                null,
                keycloakTokenWithConsentScope,
                400,
                ApplicationConstants.IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG);
    }

    @Test
    public void testDeleteConsent_MissingRequiredScope() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "J6025323",
                "IN",
                keycloakTokenWithNoScope,
                403);
    }

    @Test
    public void testDeleteConsent_ShouldWorkWithConsentAdminScope() throws Exception {
        verifyDeleteConsent(
                IdentifierTypeEnum.PASSPORT.name(),
                "J6025323",
                "IN",
                keycloakTokenWithConsentAdminScope,
                404);
    }

    @Test
    public void testDeleteConsent_RequesterOtherThanGuardian() throws Exception {
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_valid_minimal_payload1.json",
                201);
        verifyDeleteConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScopeOtherUser,
                403,
                ApplicationConstants.NOT_AUTHORIZED_ERROR_MSG);
        verifyDeleteConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                204);
    }


    /* --------------------------- Get All Consent (Reports) tests ---------------------------------------*/

    @Test
    public void testGetAllConsent_InvalidFromDate() throws Exception {
        verifyGetAllConsent(
                "2021-02-29",
                "2020-12-31",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_FROM_DATE_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_NullFromDate() throws Exception {
        verifyGetAllConsent(
                null,
                "2020-12-31",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_REPORT_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_BlankFromDate() throws Exception {
        verifyGetAllConsent(
                "",
                "2020-12-31",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_REPORT_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_InvalidToDate() throws Exception {
        verifyGetAllConsent(
                "2021-03-29",
                "2021-02-29",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_TO_DATE_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_NullToDate() throws Exception {
        verifyGetAllConsent(
                "2020-01-01",
                null,
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_REPORT_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_BlankToDate() throws Exception {
        verifyGetAllConsent(
                "2020-01-01",
                "",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_REPORT_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_InvalidFlag() throws Exception {
        verifyGetAllConsent(
                "2020-01-01",
                "2021-04-29",
                "truewewewewe",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_REPORT_FLAG_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_InvalidDateRange() throws Exception {
        verifyGetAllConsent(
                "2021-01-01",
                "2020-01-01",
                "true",
                keycloakTokenWithConsentAdminScope, 400,
                ApplicationConstants.INVALID_DATE_RANGE_CRITERIA_ERROR_MSG);
    }

    @Test
    public void testGetAllConsent_MissingRequiredScope() throws Exception {
        verifyGetAllConsent(
                "2020-01-31",
                "2020-12-31",
                "true",
                keycloakTokenWithNoScope,
                403);
    }

    @Test
    public void testGetAllConsent_WrongScope() throws Exception {
        verifyGetAllConsent(
                "2020-01-31",
                "2020-12-31",
                "true",
                keycloakTokenWithConsentScope,
                403);
    }

    /* -------------------- Positive scenarios ----------------------------------------*/

    @Test
    public void test_successful() throws Exception {
        // retrieve all existing consents to verify the increased count later
        JsonArray existingConsentsAtStart = verifyGetAllConsent(
                LocalDate.now().minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                "false",
                keycloakTokenWithConsentAdminScope,
                200);

        // create consent
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_valid_minimal_payload1.json",
                201);

        // creating consent again with same data, should fail with 409
        verifyConsentCreation(
                keycloakTokenWithConsentScope,
                "create_consent_valid_minimal_payload1.json",
                409);

        // retrieve all consents to verify the count
        JsonArray existingConsentsAfterAddingConsent = verifyGetAllConsent(
                LocalDate.now().minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                "false",
                keycloakTokenWithConsentAdminScope,
                200);
        Assertions.assertEquals(existingConsentsAtStart.size() + 1, existingConsentsAfterAddingConsent.size());

        // retrieve consent
        JsonObject consent = verifyGetConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                200);

        // verify retrieved consent
        Assertions.assertNotNull(consent);
        Assertions.assertTrue(consent.containsKey("consent_applicable_date"));
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), consent.getString("consent_applicable_date"));
        Assertions.assertTrue(consent.containsKey("birth_date"));
        Assertions.assertEquals("2005-01-01", consent.getString("birth_date"));
        Assertions.assertTrue(consent.containsKey("originating_system"));
        Assertions.assertEquals("56c0e3bc-6ba1-4914-8b4a-3e3d4b0c7f41|client-mobile-app", consent.getString("originating_system"));
        Assertions.assertFalse(consent.containsKey("consent_withdrawn_date"));

        Assertions.assertNotNull(consent.containsKey("guardian"));
        JsonObject guardian = consent.getJsonObject("guardian");
        Assertions.assertTrue(guardian.containsKey("identifier_type"));
        Assertions.assertEquals(IdentifierTypeEnum.PASSPORT.name(), guardian.getString("identifier_type"));
        Assertions.assertTrue(guardian.containsKey("identifier_value"));
        Assertions.assertEquals("P2023P20", guardian.getString("identifier_value"));
        Assertions.assertTrue(guardian.containsKey("identifier_issuing_country"));
        Assertions.assertEquals("ZA", guardian.getString("identifier_issuing_country"));

        Assertions.assertNotNull(consent.containsKey("minor"));
        JsonObject minor = consent.getJsonObject("minor");
        Assertions.assertTrue(minor.containsKey("identifier_type"));
        Assertions.assertEquals(IdentifierTypeEnum.ID.name(), minor.getString("identifier_type"));
        Assertions.assertTrue(guardian.containsKey("identifier_value"));
        Assertions.assertEquals("0501017488088", minor.getString("identifier_value"));
        Assertions.assertTrue(minor.containsKey("identifier_issuing_country"));
        Assertions.assertEquals("ZA", minor.getString("identifier_issuing_country"));

        // Revoke the consent
        verifyDeleteConsent(IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                204);

        // retrieve all consents to verify the count
        JsonArray existingConsentsAfterDeletingConsent = verifyGetAllConsent(
                LocalDate.now().minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                "false",
                keycloakTokenWithConsentAdminScope,
                200);
        Assertions.assertEquals(existingConsentsAtStart.size(), existingConsentsAfterDeletingConsent.size());

        JsonArray existingConsentsAfterDeletingConsentIncludingWithdrawn = verifyGetAllConsent(
                LocalDate.now().minusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                LocalDate.now().plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE),
                "true",
                keycloakTokenWithConsentAdminScope,
                200);
        Assertions.assertTrue(existingConsentsAfterDeletingConsentIncludingWithdrawn.stream().anyMatch(a -> {
            JsonObject jsonObject = (JsonObject) a;
            JsonObject minor1 = jsonObject.getJsonObject("minor");
            if (minor1 != null
                    && StringUtils.equals(minor1.getString("identifier_type"), IdentifierTypeEnum.ID.name())
                    && StringUtils.equals(minor1.getString("identifier_value"), "0501017488088")
                    && StringUtils.equals(minor1.getString("identifier_issuing_country"), "ZA"))
                return true;
            return false;
        }));

        // retrieve consent now should get 404
        Assertions.assertNull(verifyGetConsent(
                IdentifierTypeEnum.ID.name(),
                "0501017488088",
                "ZA",
                keycloakTokenWithConsentScope,
                404));

    }

    /* --------------------------- Util methods ---------------------------------------*/


    private void verifyConsentCreation(
            final String KeyCloakToken,
            final String jsonFileName,
            final int expectedResponseCode,
            final String... expectedErrorMessages) throws IOException {

        System.out.println(String.format("[verifyConsentCreation(%s,%s,%s,%s)]", "someToken", jsonFileName, expectedResponseCode, expectedErrorMessages));

        String requestUrl = TestUtil.getRequestUrl(
                app_base_path,
                "/consents",
                null,
                null,
                null);
        HttpPost postConsent = new HttpPost(requestUrl);
        postConsent.addHeader("Authorization", "Bearer " + KeyCloakToken);
        System.out.println("file name >>>>>>>>>>" + jsonFileName);
        String fileContent = FileUtils.readFileToString(
                new File(this.getClass().getClassLoader().getResource(jsonFileName).getFile()), "UTF-8");
        System.out.println("file content >>>>>>>>>>" + fileContent);
        StringEntity entity = new StringEntity(fileContent, ContentType.APPLICATION_JSON);
        postConsent.setEntity(entity);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(postConsent)) {

            Assertions.assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());

            if (expectedResponseCode == 401 || expectedResponseCode == 403) {
                return;
            } else if (expectedResponseCode == 201) {
                Assertions.assertNotNull(response.getHeaders("location"), "location header not present");
            } else if (expectedErrorMessages.length > 0) {
                String responseString = EntityUtils.toString(response.getEntity());
                JsonArray jsonArray = new JsonObject(responseString).getJsonArray("error_msg");

                List<String> expectedButNotFoundErrors = new ArrayList<>();
                for (String msg : expectedErrorMessages) {
                    if (!jsonArray.contains(msg)) {
                        expectedButNotFoundErrors.add(msg);
                    }
                }

                Assertions.assertTrue(
                        expectedButNotFoundErrors.isEmpty(),
                        String.format(
                                "Expected but not found errors: %s, found errors: %s, used payload: %s",
                                expectedButNotFoundErrors,
                                jsonArray,
                                jsonFileName));

                Assertions.assertEquals(expectedErrorMessages.length, jsonArray.size(), String.format(
                        "Expected errors: %s, found errors: %s, used payload: %s",
                        Arrays.asList(expectedErrorMessages),
                        jsonArray,
                        jsonFileName));
            }
        }
    }

    private JsonObject verifyGetConsent(
            String identifierType,
            String identifierValue,
            String country,
            String keyCloakToken,
            int expectedResponseCode,
            String... expectedErrorMessages) throws Exception {

        System.out.println(String.format("[verifyGetConsent(%s,%s,%s,%s,%s,%s)]", identifierType, identifierValue, country, "keyCloakToken", expectedResponseCode, expectedErrorMessages));

        String requestUrl = TestUtil.getRequestUrl(
                app_base_path,
                "/consents",
                identifierType,
                identifierValue,
                country);

        System.out.println("request path: " + requestUrl);
        HttpGet getConsent = new HttpGet(requestUrl);
        getConsent.addHeader("Authorization", "Bearer " + keyCloakToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(getConsent)) {

            Assertions.assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());

            if (expectedResponseCode == 401 || expectedResponseCode == 403) {
                return null;
            }

            String responseString = EntityUtils.toString(response.getEntity());
            Assertions.assertDoesNotThrow(() -> new JsonObject(responseString), "Invalid json response");
            JsonObject jsonResponse = new JsonObject(responseString);
            if (expectedResponseCode == 200) {
                return jsonResponse;
            } else if (expectedErrorMessages.length > 0) {
                JsonArray jsonArray = jsonResponse.getJsonArray("error_msg");

                List<String> expectedButNotFoundErrors = new ArrayList<>();
                for (String msg : expectedErrorMessages) {
                    if (!jsonArray.contains(msg)) {
                        expectedButNotFoundErrors.add(msg);
                    }
                }

                Assertions.assertTrue(
                        expectedButNotFoundErrors.isEmpty(),
                        String.format(
                                "Expected but not found errors: %s, found errors: %s, input[identifierType=%s,identifierValue=%s,country=%s]",
                                expectedButNotFoundErrors,
                                jsonArray,
                                identifierType,
                                identifierValue,
                                country));

                Assertions.assertEquals(expectedErrorMessages.length, jsonArray.size(), String.format(
                        "Expected errors: %s, found errors: %s, input supplied [identifierType=%s,identifierValue=%s,country=%s]",
                        Arrays.asList(expectedErrorMessages),
                        jsonArray,
                        identifierType,
                        identifierValue,
                        country));
            }
        }
        return null;
    }

    private JsonArray verifyGetAllConsent(
            String fromDate,
            String toDate,
            String includeWithdrawn,
            String keyCloakToken,
            int expectedResponseCode,
            String... expectedErrorMessages) throws Exception {

        System.out.println(String.format("[verifyGetAllConsent(%s,%s,%s,%s,%s,%s)]", fromDate, toDate, includeWithdrawn, "keyCloakToken", expectedResponseCode, expectedErrorMessages));

        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("from_date", fromDate);
        queryParam.put("to_date", toDate);
        queryParam.put("include_withdrawn", includeWithdrawn);
        String requestUrl = TestUtil.getRequestUrl(
                app_base_path,
                "/consents/reports", queryParam);
        HttpGet getConsent = new HttpGet(requestUrl);
        getConsent.addHeader("Authorization", "Bearer " + keyCloakToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(getConsent)) {

            Assertions.assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());

            if (expectedResponseCode == 401 || expectedResponseCode == 403) {
                return null;
            }

            String responseString = EntityUtils.toString(response.getEntity());
            if (expectedResponseCode == 200) {
                return new JsonArray(responseString);
            } else if (expectedErrorMessages.length > 0) {
                JsonArray jsonArray = new JsonObject(responseString).getJsonArray("error_msg");

                List<String> expectedButNotFoundErrors = new ArrayList<>();
                for (String msg : expectedErrorMessages) {
                    if (!jsonArray.contains(msg)) {
                        expectedButNotFoundErrors.add(msg);
                    }
                }

                Assertions.assertTrue(
                        expectedButNotFoundErrors.isEmpty(),
                        String.format(
                                "Expected but not found errors: %s, found errors: %s, input supplied [fromDate=%s, toDate=%s, includeWithdrawn=%s]",
                                expectedButNotFoundErrors,
                                jsonArray,
                                fromDate,
                                toDate,
                                includeWithdrawn));

                Assertions.assertEquals(expectedErrorMessages.length, jsonArray.size(), String.format(
                        "Expected errors: %s, found errors: %s, input supplied [fromDate=%s, toDate=%s, includeWithdrawn=%s]",
                        Arrays.asList(expectedErrorMessages),
                        jsonArray,
                        fromDate,
                        toDate,
                        includeWithdrawn));
            }
        }
        return null;
    }

    private void verifyDeleteConsent(
            String identifierType,
            String identifierValue,
            String country,
            String keyCloakToken,
            int expectedResponseCode,
            String... expectedErrorMessages) throws Exception {

        System.out.println(String.format("[verifyGetConsent(%s,%s,%s,%s,%s,%s)]", identifierType, identifierValue, country, "keyCloakToken", expectedResponseCode, expectedErrorMessages));

        String requestUrl = TestUtil.getRequestUrl(
                app_base_path,
                "/consents",
                identifierType,
                identifierValue,
                country);
        HttpDelete deleteConsent = new HttpDelete(requestUrl);
        deleteConsent.addHeader("Authorization", "Bearer " + keyCloakToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(deleteConsent)) {

            Assertions.assertEquals(expectedResponseCode, response.getStatusLine().getStatusCode());
            if (expectedResponseCode == 204) {
                return;
            } else if (expectedErrorMessages.length > 0) {
                String responseString = EntityUtils.toString(response.getEntity());
                JsonArray jsonArray = new JsonObject(responseString).getJsonArray("error_msg");

                List<String> expectedButNotFoundErrors = new ArrayList<>();
                for (String msg : expectedErrorMessages) {
                    if (!jsonArray.contains(msg)) {
                        expectedButNotFoundErrors.add(msg);
                    }
                }

                Assertions.assertTrue(
                        expectedButNotFoundErrors.isEmpty(),
                        String.format(
                                "Expected but not found errors: %s, found errors: %s, input[identifierType=%s,identifierValue=%s,country=%s]",
                                expectedButNotFoundErrors,
                                jsonArray,
                                identifierType,
                                identifierValue,
                                country));

                Assertions.assertEquals(expectedErrorMessages.length, jsonArray.size(), String.format(
                        "Expected errors: %s, found errors: %s, input supplied [identifierType=%s,identifierValue=%s,country=%s]",
                        Arrays.asList(expectedErrorMessages),
                        jsonArray,
                        identifierType,
                        identifierValue,
                        country));
            }
        }
    }


}