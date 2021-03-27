package tum.ret.rity.minor.consent.rest.representation;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BeanValidationValidationTest {

    public static final String FUTURE_DATE_STRING = "4020-01-01";
    public static final LocalDate FUTURE_DATE = LocalDate.parse(FUTURE_DATE_STRING);
    public static final LocalDate PAST_DATE = LocalDate.parse("1020-01-01");
    public static final LocalDate PRESENT_DATE = LocalDate.now();
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void validate_UserWriteRepresentation() {
        // validate correctly populated object
        Assertions.assertTrue(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", "ZA")).isEmpty());
        Assertions.assertTrue(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "ZA")).isEmpty());
        // validate non existence of non mandatory attributes
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(null, "2001014800086", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), null, "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", null)).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(null, null, "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(null, null, null)).isEmpty());
        // validate attributes values
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation("", "2001014800086", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation("   ", "2001014800086", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation("IDA", "", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "   ", "ZA")).isEmpty());

        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "", "ZA")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "  ", "ZA")).isEmpty());

        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "   ")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "AB")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "ZAR")).isEmpty());

        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", "")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", "   ")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", "AB")).isEmpty());
        Assertions.assertFalse(validator.validate(new UserWriteRepresentation(IdentifierTypeEnum.ID.name(), "2001014800086", "ZAR")).isEmpty());

    }

    @Test
    public void validate_ConsentWriteRepresentation() {
        // validate correctly populated object
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(LocalDate.parse("3020-01-01"), getValidUser(), getValidUser(), LocalDate.parse("1020-01-01"), "something")).isEmpty());
        // validate non existence of non mandatory attributes
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(LocalDate.parse("3020-01-01"), getValidUser(), getValidUser(), LocalDate.parse("1020-01-01"), null)).isEmpty());
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), getValidUser(), LocalDate.parse("1020-01-01"), "something")).isEmpty());
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(LocalDate.parse("3020-01-01"), getValidUser(), getValidUser(), null, "something")).isEmpty());
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), getValidUser(), null, null)).isEmpty());
        // validate existence of mandatory attributes
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(null, null, getValidUser(), null, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), null, null, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(null, null, null, null, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation()).isEmpty());
        // validate attributes values
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(FUTURE_DATE, getValidUser(), getValidUser(), null, null)).isEmpty());
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(PRESENT_DATE, getValidUser(), getValidUser(), null, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(PAST_DATE, getValidUser(), getValidUser(), null, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), getValidUser(), PRESENT_DATE, null)).isEmpty());
        Assertions.assertFalse(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), getValidUser(), FUTURE_DATE, null)).isEmpty());
        Assertions.assertTrue(validator.validate(new ConsentWriteRepresentation(null, getValidUser(), getValidUser(), PAST_DATE, null)).isEmpty());

    }

    private UserWriteRepresentation getValidUser() {
        return new UserWriteRepresentation(IdentifierTypeEnum.PASSPORT.name(), "sddsd", "ZA");
    }

    private ConsentWriteRepresentation getValidRepresentation() {
        ConsentWriteRepresentation representation = new ConsentWriteRepresentation();
        representation.setMinor(getValidUser());
        representation.setGuardian(getValidUser());
        return representation;
    }

    @Test
    public void validate_additionalValidation4() {
        // consent_applicable_date is set as current date if not provided
        ConsentWriteRepresentation representation = getValidRepresentation();
        representation.setOriginatingSystem("qwqw");
        representation.setBirthDate("2018-01-01");
        Consent domainObject = representation.getDomainObject(null);
        assertTrue(domainObject.getConsentApplicableDate().compareTo(LocalDate.now()) == 0);
    }

    //
    @Test
    public void validate_additionalValidation5() {
        // if originating_system is not provided it would be derived from claims
        ConsentWriteRepresentation representation = getValidRepresentation();
        representation.setBirthDate("2018-01-01");
        representation.setClientAppNameClaim("mobile");
        representation.setSubjectClaim("sub");
        Consent domainObject = representation.getDomainObject(null);
        assertEquals("sub|mobile", domainObject.getOriginatingSystem());
    }

    //
//
    @Test
    public void validate_additionalValidation5b() {
        // if originating_system is provided and claims exist it would be derived from claims
        ConsentWriteRepresentation representation = getValidRepresentation();
        representation.setOriginatingSystem("sasdd");
        representation.setOriginatingSystem("sasdd");
        representation.setBirthDate("2018-01-01");
        representation.setClientAppNameClaim("mobile");
        Consent domainObject = representation.getDomainObject(null);
        assertEquals("sasdd", domainObject.getOriginatingSystem());
    }

    //
    @Test
    public void validate_additionalValidation5c() {
        // if originating_system is not provided it would be derived from claims
        ConsentWriteRepresentation representation = getValidRepresentation();
        representation.setOriginatingSystem("sasdd");
        representation.setClientAppNameClaim("mobile");
        representation.setOriginatingSystem("sasdd");
        representation.setBirthDate("2018-01-01");
        Consent domainObject = representation.getDomainObject(null);
        assertEquals("sasdd", domainObject.getOriginatingSystem());
    }
//
//    @Test
//    public void validate_additionalValidation6() {
//        // if claims not available originating system should be provided
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setBirthDate("2018-01-01");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void validate_additionalValidation7() {
//        // if originating system is not provided, both claims are required
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setBirthDate("2018-01-01");
//        representation.setConsentGranterUsernameClaim("mkumar");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void validate_additionalValidation8() {
//        // if minor identifier type is ID, and birthDate is not supplied, it should not throw error
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setOriginatingSystem("qwqw");
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setConsentGranterPassportNumberClaim("sddsd");
//        representation.getMinor().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getMinor().setIdentifierValue("0512315798086");
//        Consent domainObject = representation.getDomainObject(null);
//        assertEquals("2005-12-31",domainObject.getBirthDateAsString());
//    }
//
//    @Test
//    public void validate_additionalValidation9() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setOriginatingSystem("qwqw");
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.getMinor().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getMinor().setIdentifierValue("9812318394086");
//        representation.setBirthDate("2018-01-01");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void test_guardian_validations_idAndDobMissingInToken() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//
//        representation.getGuardian().setIdentifierType(IdentifierTypeEnum.PASSPORT.name());
//        representation.getGuardian().setIdentifierValue("asasasasas");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void test_guardian_validations_idAndDobMismatch() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setOriginatingSystem("qwqw");
//        representation.setBirthDate("2018-01-01");
//
//        representation.getGuardian().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getGuardian().setIdentifierValue("0501017488088");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void test_guardian_validations_id_mismatch() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setConsentGranterIdNumberClaim("9812318394086");
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setOriginatingSystem("qwqw");
//
//        representation.getGuardian().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getGuardian().setIdentifierValue("0601018200084");
//        representation.setBirthDate("2018-01-01");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void test_guardian_validations_invalidId() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setConsentGranterIdNumberClaim("981231839086");
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setOriginatingSystem("qwqw");
//
//        representation.getGuardian().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getGuardian().setIdentifierValue("981231839086");
//        representation.setBirthDate("2018-01-01");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }
//
//    @Test
//    public void test_minor_validations_invalidId() {
//        // if minor identifier type is ID, and wrong birthDate is supplied
//        ConsentWriteRepresentation representation = getValidRepresentation();
//        representation.setConsentGranterIdNumberClaim("9812318439086");
//        representation.setConsentGranterBirthDateClaim("19850101");
//        representation.setOriginatingSystem("qwqw");
//
//        representation.getGuardian().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getGuardian().setIdentifierValue("9812318439086");
//        representation.setBirthDate("2018-01-01");
//        representation.getMinor().setIdentifierType(IdentifierTypeEnum.ID.name());
//        representation.getMinor().setIdentifierValue("981231839086");
//        assertThrows(BadRequestException.class, () -> representation.getDomainObject(null));
//    }

}