package tum.ret.rity.minor.consent.service;

import org.eclipse.microprofile.jwt.ClaimValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;
import tum.ret.rity.minor.consent.infrastructure.exception.*;
import tum.ret.rity.minor.consent.repository.ConsentRepository;
import tum.ret.rity.minor.consent.rest.representation.ConsentReadRepresentation;
import tum.ret.rity.minor.consent.rest.representation.ConsentWriteRepresentation;
import tum.ret.rity.minor.consent.rest.representation.UserWriteRepresentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ConsentApplicationServiceTest {

    @Mock
    ConsentRepository repository;
    @Mock
    private Logger log;
    @Mock
    ClaimValue<String> scope;
    @InjectMocks
    ConsentApplicationService service;
    String referenceNumber = "000011111100000";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addConsent_rowAddedZero() throws ConsentAlreadyExistException, UnrecoverableException {
        // set expectation
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .build();

        Mockito.when(repository.addMinorConsent(consent)).thenThrow(new UnrecoverableException(referenceNumber));

        // set given
        ConsentWriteRepresentation consentWriteRepresentation
                = ConsentWriteRepresentation.builder()
                .birthDate("2015-01-01")
                .consentApplicableDate("2020-01-01")
                .originatingSystem("originatingSystem")
                .guardian(UserWriteRepresentation.builder()
                        .identifierType(IdentifierTypeEnum.ID.name())
                        .identifierValue("8501017267088")
                        .countryOfIssue("ZA").build())
                .minor(UserWriteRepresentation.builder()
                        .identifierType(IdentifierTypeEnum.ID.name())
                        .identifierValue("1501019270087")
                        .countryOfIssue("ZA").build())
                .build();

        service.setConsentGranterIdNumberClaim("8501017267088");

        // verify
        UnrecoverableException exception
                = assertThrows(UnrecoverableException.class, () -> service.addConsent(consentWriteRepresentation));
        assertEquals(ApplicationConstants.GENERIC_EXCEPTION_MSG, exception.getMessage());
    }

    @Test
    void getConsent_invalidInputs() {
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501017267088", ""));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501017267088", "   "));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501017267088", "AB"));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501017267088", null));

        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "", "ZA"));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", " ", "ZA"));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", null, "ZA"));
        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501asd017267088", "ZA"));

        assertThrows(BadRequestException.class, () -> service.getConsent("", "8501017267088", "ZA"));
        assertThrows(BadRequestException.class, () -> service.getConsent("  ", "8501017267088", "ZA"));
        assertThrows(BadRequestException.class, () -> service.getConsent(null, "8501017267088", "ZA"));

        assertThrows(BadRequestException.class, () -> service.getConsent(null, null, null));
        assertThrows(BadRequestException.class, () -> service.getConsent("", "", ""));
        assertThrows(BadRequestException.class, () -> service.getConsent("  ", "  ", "  "));

        assertThrows(BadRequestException.class, () -> service.getConsent("ID", "8501017267088", "IN"));
    }

    @Test
    void getConsent_notFound() throws NoConsentFoundException {
        // set expectation
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenThrow(new NoConsentFoundException(referenceNumber));
        // verify
        assertThrows(NoConsentFoundException.class, () -> service.getConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void getConsent_success() throws NoConsentFoundException {
        // set expectation
        service.setConsentGranterIdNumberClaim("121");
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenReturn(Consent.builder().guardian(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("121").countryOfIssue("IN").build()).build());
        Mockito.when(scope.getValue()).thenReturn("consent");
        // verify
        assertDoesNotThrow(() -> service.getConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void revokeConsent_invalidInputs() {
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501017267088", ""));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501017267088", "   "));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501017267088", "AB"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501017267088", null));

        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "", "ZA"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", " ", "ZA"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", null, "ZA"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501asd017267088", "ZA"));

        assertThrows(BadRequestException.class, () -> service.revokeConsent("", "8501017267088", "ZA"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("  ", "8501017267088", "ZA"));
        assertThrows(BadRequestException.class, () -> service.revokeConsent(null, "8501017267088", "ZA"));

        assertThrows(BadRequestException.class, () -> service.revokeConsent(null, null, null));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("", "", ""));
        assertThrows(BadRequestException.class, () -> service.revokeConsent("  ", "  ", "  "));

        assertThrows(BadRequestException.class, () -> service.revokeConsent("ID", "8501017267088", "IN"));
    }

    @Test
    void revokeConsent_notFound() throws NoConsentFoundException {
        // set expectation
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenThrow(new NoConsentFoundException(referenceNumber));
        Mockito.doThrow(new NoConsentFoundException(referenceNumber)).when(repository).revokeConsent("ID", "8501017267088", "ZA");
        // verify
        assertThrows(NoConsentFoundException.class, () -> service.revokeConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void revokeConsent_requestedByOtherThanGuardianId() throws NoConsentFoundException {
        // given
        service.setConsentGranterIdNumberClaim("1501019270087");
        // set expectation
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .build();
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenReturn(consent);
        // verify
        assertThrows(ForbiddenException.class, () -> service.revokeConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void getConsent_requestedByOtherThanGuardianId() throws NoConsentFoundException {
        // given
        service.setConsentGranterIdNumberClaim("1501019270087");
        // set expectation
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .build();
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenReturn(consent);
        // verify
        assertThrows(ForbiddenException.class, () -> service.getConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void revokeConsent_requestedByOtherThanGuardianPassport() throws NoConsentFoundException {
        // given
        service.setConsentGranterPassportNumberClaim("1501019270087");
        // set expectation
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.PASSPORT).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .build();
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenReturn(consent);
        // verify
        assertThrows(ForbiddenException.class, () -> service.revokeConsent("ID", "8501017267088", "ZA"));
    }

    @Test
    void revokeConsent_success() throws NoConsentFoundException, BadRequestException, ForbiddenException {
        // given
        service.setConsentGranterPassportNumberClaim("8501017267088");
        // set expectation
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.PASSPORT).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .build();
        Mockito.when(repository.getConsent("ID", "8501017267088", "ZA"))
                .thenReturn(consent);
        Mockito.doNothing().when(repository).revokeConsent("ID", "8501017267088", "ZA");
        service.revokeConsent("ID", "8501017267088", "ZA");
        // verify
        Mockito.verify(repository).revokeConsent("ID", "8501017267088", "ZA");
    }

    @Test
    void getAllConsent_invalidDates() {
        assertThrows(BadRequestException.class, () -> service.getAllConsent("", "", "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent(null, null, "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent("", "2020-01-01", "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent("2020-01-01", "", "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent(null, "2020-01-01", "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent("2020-01-01", null, "true"));
        assertThrows(BadRequestException.class, () -> service.getAllConsent("2020-01-01", "2019-01-01", "true"));
    }

    @Test
    void getAllConsent_success() throws BadRequestException {
        Consent consent = Consent.builder()
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardian(User.builder().identifierType(IdentifierTypeEnum.PASSPORT).identifierValue("8501017267088").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .minor(User.builder().identifierType(IdentifierTypeEnum.ID).identifierValue("1501019270087").countryOfIssue(ApplicationConstants.SOUTH_AFRICA_COUNTRY_CODE).build())
                .birthDate(LocalDate.parse("2015-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .originatingSystem("originatingSystem")
                .consentRequestDate(LocalDateTime.parse("2020-01-01T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .consentWithdrawnDate(LocalDateTime.parse("2020-01-02T10:25:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        Collection<Consent> consentCollection = new ArrayList<>();
        consentCollection.add(consent);
        Mockito.when(
                repository.getAllConsent(
                        LocalDate.parse("2019-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                        LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE),
                        true))
                .thenReturn(consentCollection);
        Collection<ConsentReadRepresentation> allConsent = service.getAllConsent("2019-01-01", "2020-01-01", "true");
        assertEquals(1, allConsent.size());
        ConsentReadRepresentation next = allConsent.iterator().next();
        assertEquals("2020-01-02T10:25:30", next.getConsentWithdrawnDate());
    }
}