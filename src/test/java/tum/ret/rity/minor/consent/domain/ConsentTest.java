package tum.ret.rity.minor.consent.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ConsentTest {

    @Test
    void testUtilityMethods() {
        assertNull(Consent.builder().birthDate(null).build().getBirthDateAsString());
        assertNull(Consent.builder().consentApplicableDate(null).build().getConsentApplicableDateAsString());
        assertNull(Consent.builder().consentWithdrawnDate(null).build().getConsentWithdrawnDateAsString());
        assertNull(Consent.builder().consentRequestDate(null).build().getConsentRequestDateAsString());
    }

}