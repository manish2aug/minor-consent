package tum.ret.rity.minor.consent.repository.impl;

import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;
import tum.ret.rity.minor.consent.infrastructure.exception.ConsentAlreadyExistException;
import tum.ret.rity.minor.consent.infrastructure.exception.NoConsentFoundException;
import tum.ret.rity.minor.consent.infrastructure.exception.UnrecoverableException;
import tum.ret.rity.minor.consent.persistence.dto.ConsentDTO;
import tum.ret.rity.minor.consent.persistence.mappers.ConsentMapper;

import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.core.HttpHeaders;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

class ConsentRepositoryImplTest {

    @InjectMocks
    private ConsentRepositoryImpl repository;
    @Mock
    private ConsentMapper mockMapper;
    @Mock
    private HttpHeaders mockHeazders;
    @Mock
    private ManagedExecutorService managedExecutorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMinorConsent() {
        LocalDate localDate = LocalDate.of(2011, 12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(2011, 12, 12, 1, 1, 1);
        Consent consent = Consent.builder()
                .consentApplicableDate(localDate)
                .consentWithdrawnDate(localDateTime)
                .consentRequestDate(localDateTime)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("1234")
                        .countryOfIssue("SA")
                        .build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.PASSPORT)
                        .identifierValue("abcd")
                        .countryOfIssue("ZA")
                        .build())
                .birthDate(localDate)
                .originatingSystem("originatingSystem")
                .build();
        ConsentDTO consentDto = consent.getConsentDto();
        Mockito.when(mockMapper.insert(consentDto)).thenReturn(1);
        Assertions.assertEquals(1, repository.addMinorConsent(consent));
    }

    @Test
    void addMinorConsent_alreadyExist() {
        LocalDate localDate = LocalDate.of(2011, 12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(2011, 12, 12, 1, 1, 1);
        Consent consent = Consent.builder()
                .consentApplicableDate(localDate)
                .consentWithdrawnDate(localDateTime)
                .consentRequestDate(localDateTime)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("1234")
                        .countryOfIssue("SA")
                        .build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.PASSPORT)
                        .identifierValue("abcd")
                        .countryOfIssue("ZA")
                        .build())
                .birthDate(localDate)
                .originatingSystem("originatingSystem")
                .build();
        ConsentDTO consentDto = consent.getConsentDto();
        PersistenceException exception = new PersistenceException("already exists");
        Mockito.when(mockMapper.insert(consentDto)).thenThrow(exception);
        Assertions.assertThrows(ConsentAlreadyExistException.class, () -> repository.addMinorConsent(consent));
    }

    @Test
    void addMinorConsent_otherPersistenceException() {
        LocalDate localDate = LocalDate.of(2011, 12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(2011, 12, 12, 1, 1, 1);
        Consent consent = Consent.builder()
                .consentApplicableDate(localDate)
                .consentWithdrawnDate(localDateTime)
                .consentRequestDate(localDateTime)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("1234")
                        .countryOfIssue("SA")
                        .build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.PASSPORT)
                        .identifierValue("abcd")
                        .countryOfIssue("ZA")
                        .build())
                .birthDate(localDate)
                .originatingSystem("originatingSystem")
                .build();
        ConsentDTO consentDto = consent.getConsentDto();
        PersistenceException exception = new PersistenceException("abcd");
        Mockito.when(mockMapper.insert(consentDto)).thenThrow(exception);
        Assertions.assertThrows(UnrecoverableException.class, () -> repository.addMinorConsent(consent));
    }

    @Test
    void addMinorConsent_notAdded() {
        LocalDate localDate = LocalDate.of(2011, 12, 12);
        LocalDateTime localDateTime = LocalDateTime.of(2011, 12, 12, 1, 1, 1);
        Consent consent = Consent.builder()
                .consentApplicableDate(localDate)
                .consentWithdrawnDate(localDateTime)
                .consentRequestDate(localDateTime)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.ID)
                        .identifierValue("1234")
                        .countryOfIssue("SA")
                        .build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.PASSPORT)
                        .identifierValue("abcd")
                        .countryOfIssue("ZA")
                        .build())
                .birthDate(localDate)
                .originatingSystem("originatingSystem")
                .build();
        ConsentDTO consentDto = consent.getConsentDto();
        Mockito.when(mockMapper.insert(consentDto)).thenReturn(0);
        Assertions.assertThrows(UnrecoverableException.class, () -> repository.addMinorConsent(consent));
    }

    @Test
    void getConsent_found() {
        ConsentDTO consentDTO = ConsentDTO.builder()
                .consentApplicableDate(LocalDate.now())
                .consentWithdrawnDate(LocalDateTime.now())
                .consentRequestDate(LocalDateTime.now())
                .guardianIdentifierType(IdentifierTypeEnum.ID.name())
                .guardianIdentifierValue("someId")
                .guardianIdentifierIssuingCountry("AB")
                .minorIdentifierType(IdentifierTypeEnum.PASSPORT.name())
                .minorIdentifierValue("sdsdad")
                .minorIdentifierIssuingCountry("sdasd")
                .minorDateOfBirth(LocalDate.now())
                .originatingSystem("originatingSystem")
                .build();
        Mockito.when(mockMapper.select("A", "B", "C")).thenReturn(consentDTO);
        Consent consent = repository.getConsent("A", "B", "C");
        Assertions.assertEquals(LocalDate.now(), consent.getConsentApplicableDate());
        Assertions.assertEquals(LocalDate.now().toString(), consent.getConsentWithdrawnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(LocalDate.now().toString(), consent.getConsentRequestDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        Assertions.assertEquals(LocalDate.now(), consent.getBirthDate());
        Assertions.assertEquals("originatingSystem", consent.getOriginatingSystem());
        Assertions.assertEquals(IdentifierTypeEnum.ID, consent.getGuardian().getIdentifierType());
        Assertions.assertEquals(IdentifierTypeEnum.PASSPORT, consent.getMinor().getIdentifierType());
        Assertions.assertEquals("someId", consent.getGuardian().getIdentifierValue());
        Assertions.assertEquals("sdsdad", consent.getMinor().getIdentifierValue());
        Assertions.assertEquals("AB", consent.getGuardian().getCountryOfIssue());
        Assertions.assertEquals("sdasd", consent.getMinor().getCountryOfIssue());
    }

    @Test
    void getConsent_notFound() {
        Mockito.when(mockMapper.select("", "", "")).thenReturn(null);
        Assertions.assertThrows(NoConsentFoundException.class, () -> repository.getConsent("", "", ""));
    }

    @Test
    void revokeConsent_deletedZero() {
        Mockito.when(mockMapper.delete("ID", "21341234", "IN")).thenReturn(0);
        Assertions.assertThrows(NoConsentFoundException.class, () -> repository.revokeConsent("ID", "21341234", "IN"));
    }

    @Test
    void revokeConsent_notFound() {
        Mockito.when(mockMapper.delete("", "", "")).thenReturn(0);
        Assertions.assertThrows(NoConsentFoundException.class, () -> repository.getConsent("", "", ""));
    }

    @Test
    void revokeConsent_success() {
        Mockito.when(mockMapper.delete("", "", "")).thenReturn(1);
        repository.revokeConsent("", "", "");
        Mockito.verify(mockMapper).delete("", "", "");
    }

    @Test
    void getAllConsent() {
        LocalDate fromDate = LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate toDate = LocalDate.parse("2020-12-31", DateTimeFormatter.ISO_LOCAL_DATE);
        boolean includeWithdrawn = false;

        ConsentDTO existingConsent = ConsentDTO
                .builder()
                .minorIdentifierType("ID")
                .minorIdentifierValue("122333")
                .minorIdentifierIssuingCountry("IN")
                .minorDateOfBirth(LocalDate.parse("2005-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardianIdentifierType("ID")
                .guardianIdentifierValue("4444")
                .guardianIdentifierIssuingCountry("ZA")
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .consentRequestDate(LocalDateTime.parse("2020-01-01T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .originatingSystem("abcd")
                .build();
        Collection<ConsentDTO> existingConsents = Collections.singleton(existingConsent);
        Mockito.when(mockMapper.selectAll(fromDate, toDate)).thenReturn(existingConsents);

        Mockito
                .doAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        ((Runnable) args[0]).run();
                        return null;
                    }
                }).when(managedExecutorService).execute(Mockito.any(Runnable.class));

        Collection<Consent> allConsent = repository.getAllConsent(fromDate, toDate, includeWithdrawn);

        Assertions.assertNotNull(allConsent);
        Assertions.assertEquals(1, allConsent.size());
        Consent retrievedConsent = allConsent.iterator().next();
        System.out.println(retrievedConsent);
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedConsent.getMinor().getIdentifierType());
        Assertions.assertEquals("122333", retrievedConsent.getMinor().getIdentifierValue());
        Assertions.assertEquals("IN", retrievedConsent.getMinor().getCountryOfIssue());
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedConsent.getGuardian().getIdentifierType());
        Assertions.assertEquals("4444", retrievedConsent.getGuardian().getIdentifierValue());
        Assertions.assertEquals("ZA", retrievedConsent.getGuardian().getCountryOfIssue());
        Assertions.assertEquals("2005-01-01", retrievedConsent.getBirthDateAsString());
        Assertions.assertEquals("2020-01-01", retrievedConsent.getConsentApplicableDateAsString());
        Assertions.assertEquals("2020-01-01T10:15:30", retrievedConsent.getConsentRequestDateAsString());
        Assertions.assertEquals("abcd", retrievedConsent.getOriginatingSystem());


        Mockito.verify(mockMapper, Mockito.never()).selectAllWithdrawn(fromDate, toDate);
    }

    @Test
    void getAllConsent_completionException() {
        LocalDate fromDate = LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate toDate = LocalDate.parse("2020-12-31", DateTimeFormatter.ISO_LOCAL_DATE);
        boolean includeWithdrawn = false;

        ConsentDTO existingConsent = ConsentDTO
                .builder()
                .minorIdentifierType("ID")
                .minorIdentifierValue("122333")
                .minorIdentifierIssuingCountry("IN")
                .minorDateOfBirth(LocalDate.parse("2005-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardianIdentifierType("ID")
                .guardianIdentifierValue("4444")
                .guardianIdentifierIssuingCountry("ZA")
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .consentRequestDate(LocalDateTime.parse("2020-01-01T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .originatingSystem("abcd")
                .build();
        Mockito.when(mockMapper.selectAll(fromDate, toDate)).thenReturn(null);

        Mockito
                .doAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        ((Runnable) args[0]).run();
                        return null;
                    }
                }).when(managedExecutorService).execute(Mockito.any(Runnable.class));

        Collection<Consent> allConsent = repository.getAllConsent(fromDate, toDate, includeWithdrawn);

        Assertions.assertNotNull(allConsent);
        Assertions.assertEquals(0, allConsent.size());
    }

    @Test
    void getAllConsent_includingWithdrawn() throws InterruptedException {
        LocalDate fromDate = LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate toDate = LocalDate.parse("2020-12-31", DateTimeFormatter.ISO_LOCAL_DATE);
        boolean includeWithdrawn = true;

        ConsentDTO existingConsent = ConsentDTO
                .builder()
                .minorIdentifierType("ID")
                .minorIdentifierValue("122333")
                .minorIdentifierIssuingCountry("IN")
                .minorDateOfBirth(LocalDate.parse("2005-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardianIdentifierType("ID")
                .guardianIdentifierValue("4444")
                .guardianIdentifierIssuingCountry("ZA")
                .consentApplicableDate(LocalDate.parse("2020-01-01", DateTimeFormatter.ISO_LOCAL_DATE))
                .consentRequestDate(LocalDateTime.parse("2020-01-01T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .originatingSystem("abcd")
                .build();
        Collection<ConsentDTO> existingConsents = Collections.singleton(existingConsent);
        Mockito.when(mockMapper.selectAll(fromDate, toDate)).thenReturn(existingConsents);

        Mockito
                .doAnswer(new Answer() {
                    public Object answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        ((Runnable) args[0]).run();
                        return null;
                    }
                }).when(managedExecutorService).execute(Mockito.any(Runnable.class));

        ConsentDTO withdrawnConsent = ConsentDTO
                .builder()
                .minorIdentifierType("ID")
                .minorIdentifierValue("122334")
                .minorIdentifierIssuingCountry("ZA")
                .minorDateOfBirth(LocalDate.parse("2005-01-02", DateTimeFormatter.ISO_LOCAL_DATE))
                .guardianIdentifierType("ID")
                .guardianIdentifierValue("4445")
                .guardianIdentifierIssuingCountry("IN")
                .consentApplicableDate(LocalDate.parse("2020-01-02", DateTimeFormatter.ISO_LOCAL_DATE))
                .consentRequestDate(LocalDateTime.parse("2020-01-02T10:15:30", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .originatingSystem("abcde")
                .build();
        Collection<ConsentDTO> withdrawnConsents = Collections.singleton(withdrawnConsent);
        Mockito.when(mockMapper.selectAllWithdrawn(fromDate, toDate)).thenReturn(withdrawnConsents);

        Mockito.doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((Runnable) args[0]).run();
            return null;
        }).when(managedExecutorService).execute(Mockito.any(Runnable.class));

        Collection<Consent> allConsent = repository.getAllConsent(fromDate, toDate, includeWithdrawn);

        Assertions.assertNotNull(allConsent);
        Assertions.assertEquals(2, allConsent.size());
        Iterator<Consent> iterator = allConsent.iterator();
        Consent retrievedConsent = iterator.next();
        System.out.println(retrievedConsent);
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedConsent.getMinor().getIdentifierType());
        Assertions.assertEquals("122333", retrievedConsent.getMinor().getIdentifierValue());
        Assertions.assertEquals("IN", retrievedConsent.getMinor().getCountryOfIssue());
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedConsent.getGuardian().getIdentifierType());
        Assertions.assertEquals("4444", retrievedConsent.getGuardian().getIdentifierValue());
        Assertions.assertEquals("ZA", retrievedConsent.getGuardian().getCountryOfIssue());
        Assertions.assertEquals("2005-01-01", retrievedConsent.getBirthDateAsString());
        Assertions.assertEquals("2020-01-01", retrievedConsent.getConsentApplicableDateAsString());
        Assertions.assertEquals("2020-01-01T10:15:30", retrievedConsent.getConsentRequestDateAsString());
        Assertions.assertEquals("abcd", retrievedConsent.getOriginatingSystem());

        Consent retrievedWithdrawnConsent = iterator.next();
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedWithdrawnConsent.getMinor().getIdentifierType());
        Assertions.assertEquals("122334", retrievedWithdrawnConsent.getMinor().getIdentifierValue());
        Assertions.assertEquals("ZA", retrievedWithdrawnConsent.getMinor().getCountryOfIssue());
        Assertions.assertEquals(IdentifierTypeEnum.ID, retrievedWithdrawnConsent.getGuardian().getIdentifierType());
        Assertions.assertEquals("4445", retrievedWithdrawnConsent.getGuardian().getIdentifierValue());
        Assertions.assertEquals("IN", retrievedWithdrawnConsent.getGuardian().getCountryOfIssue());
        Assertions.assertEquals("2005-01-02", retrievedWithdrawnConsent.getBirthDateAsString());
        Assertions.assertEquals("2020-01-02", retrievedWithdrawnConsent.getConsentApplicableDateAsString());
        Assertions.assertEquals("2020-01-02T10:15:30", retrievedWithdrawnConsent.getConsentRequestDateAsString());
        Assertions.assertEquals("abcde", retrievedWithdrawnConsent.getOriginatingSystem());


    }
}