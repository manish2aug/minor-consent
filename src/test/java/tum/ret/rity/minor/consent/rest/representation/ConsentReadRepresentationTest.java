package tum.ret.rity.minor.consent.rest.representation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class ConsentReadRepresentationTest {

    @Test
    void getReadRepresentation() {
        LocalDate now = LocalDate.now();
        Consent consent
                = Consent
                .builder()
                .birthDate(now)
                .consentApplicableDate(now)
                .consentRequestDate(LocalDateTime.now())
                .originatingSystem("originatingSystem")
                .birthDate(now)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("2341234234")
                        .countryOfIssue("ZA").build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("2341234234")
                        .countryOfIssue("ZA").build())
                .build();
        ConsentReadRepresentation readRepresentation = ConsentReadRepresentation.getReadRepresentation(consent);
        Assertions.assertEquals(now.toString(), readRepresentation.getBirthDate());
        Assertions.assertEquals(now.toString(), readRepresentation.getConsentApplicableDate());
        Collection<Consent> consentCollection = new ArrayList<>();
        consentCollection.add(consent);
        Collection<ConsentReadRepresentation> readRepresentation1 = ConsentReadRepresentation.getReadRepresentation(consentCollection);
        Assertions.assertEquals(1, readRepresentation1.size());
        ConsentReadRepresentation next = readRepresentation1.iterator().next();
        Assertions.assertEquals(now.toString(), next.getBirthDate());
        Assertions.assertEquals(now.toString(), next.getConsentApplicableDate());
    }

    @Test
    void getReadRepresentationCollection() {
        LocalDate now = LocalDate.now();
        Consent consent
                = Consent
                .builder()
                .birthDate(now)
                .consentApplicableDate(now)
                .consentRequestDate(LocalDateTime.now())
                .originatingSystem("originatingSystem")
                .birthDate(now)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("2341234234")
                        .countryOfIssue("ZA").build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("2341234234")
                        .countryOfIssue("ZA").build())
                .build();
        Collection<Consent> singleton = Collections.singletonList(consent);

        Collection<ConsentReadRepresentation> readRepresentationCollection = ConsentReadRepresentation.getReadRepresentation(singleton);
        Assertions.assertEquals(1, readRepresentationCollection.size());
        ConsentReadRepresentation readRepresentation = readRepresentationCollection.iterator().next();
        Assertions.assertEquals(now.toString(), readRepresentation.getBirthDate());
        Assertions.assertEquals(now.toString(), readRepresentation.getConsentApplicableDate());
        Collection<Consent> consentCollection = new ArrayList<>();
        consentCollection.add(consent);
        Collection<ConsentReadRepresentation> readRepresentation1 = ConsentReadRepresentation.getReadRepresentation(consentCollection);
        Assertions.assertEquals(1, readRepresentation1.size());
        ConsentReadRepresentation next = readRepresentation1.iterator().next();
        Assertions.assertEquals(now.toString(), next.getBirthDate());
        Assertions.assertEquals(now.toString(), next.getConsentApplicableDate());
    }

    @Test
    void getReadRepresentation_nullConsent() {
        Consent consent = null;
        ConsentReadRepresentation readRepresentation = ConsentReadRepresentation.getReadRepresentation(consent);
        Assertions.assertNull(readRepresentation);
    }

    @Test
    void getReadRepresentationCollection_nullCollection() {
        Collection<Consent> collection = null;
        Collection<ConsentReadRepresentation> readRepresentationCollection = ConsentReadRepresentation.getReadRepresentation(collection);
        Assertions.assertNotNull(readRepresentationCollection);
        Assertions.assertTrue(readRepresentationCollection.isEmpty());
    }

    @Test
    void getReadRepresentationCollection_emptyCollection() {
        Collection<Consent> consents = Collections.emptyList();
        Collection<ConsentReadRepresentation> readRepresentationCollection = ConsentReadRepresentation.getReadRepresentation(consents);
        Assertions.assertNotNull(readRepresentationCollection);
        Assertions.assertTrue(readRepresentationCollection.isEmpty());
    }

}