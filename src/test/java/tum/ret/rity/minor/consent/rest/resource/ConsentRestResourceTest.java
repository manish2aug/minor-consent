package tum.ret.rity.minor.consent.rest.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tum.ret.rity.minor.consent.infrastructure.exception.BadRequestException;
import tum.ret.rity.minor.consent.infrastructure.exception.ForbiddenException;
import tum.ret.rity.minor.consent.infrastructure.exception.NoConsentFoundException;
import tum.ret.rity.minor.consent.rest.representation.ConsentReadRepresentation;
import tum.ret.rity.minor.consent.rest.representation.ConsentWriteRepresentation;
import tum.ret.rity.minor.consent.rest.representation.UserWriteRepresentation;
import tum.ret.rity.minor.consent.service.ConsentApplicationService;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

class ConsentRestResourceTest {

    String referenceNumber = "000011111100000";
    @Mock
    ConsentApplicationService service;
    @Mock
    private Logger log;
    @Mock
    private UriInfo uriInfo;
    @Mock
    private UriBuilder uriBuilder;
    @InjectMocks
    ConsentRestResource resource;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getConsent_success() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.when(service.getConsent("a", "b", "c")).thenReturn(consentReadRepresentation);
        Response response = resource.getConsent("a", "b", "c");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        Assertions.assertNotNull(entity);
        ConsentReadRepresentation representation = (ConsentReadRepresentation) entity;
        Assertions.assertEquals("2020-01-01", representation.getConsentRequestDate());

        Mockito.when(service.getConsent("d", "e", "f")).thenReturn(null);
        response = resource.getConsent("d", "e", "f");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus());
        entity = response.getEntity();
        Assertions.assertNull(entity);
    }

    @Test
    void getConsent_notFound() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.when(service.getConsent("a", "b", "c")).thenThrow(new NoConsentFoundException("error"));
        Assertions.assertThrows(NoConsentFoundException.class, () -> resource.getConsent("a", "b", "c"));
    }

    @Test
    void getConsent_notForbidden() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.when(service.getConsent("a", "b", "c")).thenThrow(new ForbiddenException("error", referenceNumber));
        Assertions.assertThrows(ForbiddenException.class, () -> resource.getConsent("a", "b", "c"));
    }

    @Test
    void getConsent_notBadRequestException() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.when(service.getConsent("a", "b", "c")).thenThrow(new BadRequestException("error", referenceNumber));
        Assertions.assertThrows(BadRequestException.class, () -> resource.getConsent("a", "b", "c"));
    }

    @Test
    void getAllConsents() throws BadRequestException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.when(service.getAllConsent("2020-01-01", "2020-12-01", "true")).thenReturn(Collections.singletonList(consentReadRepresentation));

        Response response = resource.getAllConsents("2020-01-01", "2020-12-01", "true");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        Assertions.assertNotNull(entity);
        Collection<ConsentReadRepresentation> representations = (Collection<ConsentReadRepresentation>) entity;
        Assertions.assertNotNull(representations);
        Assertions.assertFalse(representations.isEmpty());
        Assertions.assertEquals(1, representations.size());
        ConsentReadRepresentation representation = representations.iterator().next();
        Assertions.assertEquals("2020-01-01", representation.getConsentRequestDate());
    }

    @Test
    void getAllConsents_empty() throws BadRequestException {
        Mockito.when(service.getAllConsent("2020-01-01", "2020-12-01", "true")).thenReturn(Collections.emptyList());

        Response response = resource.getAllConsents("2020-01-01", "2020-12-01", "true");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus());
        Object entity = response.getEntity();
        Assertions.assertNotNull(entity);
        Collection<ConsentReadRepresentation> representations = (Collection<ConsentReadRepresentation>) entity;
        Assertions.assertNotNull(representations);
        Assertions.assertTrue(representations.isEmpty());
    }

    @Test
    void revokeConsent() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.doNothing().when(service).revokeConsent("a", "b", "c");

        Response response = resource.revokeConsent("a", "b", "c");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(204, response.getStatus());
        Object entity = response.getEntity();
        Assertions.assertNull(entity);

    }

    @Test
    void revokeConsent_notFound() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.doThrow(new NoConsentFoundException("error")).when(service).revokeConsent("a", "b", "c");
        Assertions.assertThrows(NoConsentFoundException.class, () -> resource.revokeConsent("a", "b", "c"));
    }

    @Test
    void revokeConsent_notForbidden() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.doThrow(new ForbiddenException("error", referenceNumber)).when(service).revokeConsent("a", "b", "c");
        Assertions.assertThrows(ForbiddenException.class, () -> resource.revokeConsent("a", "b", "c"));
    }

    @Test
    void revokeConsent_notBadRequestException() throws ForbiddenException, BadRequestException, NoConsentFoundException {
        ConsentReadRepresentation consentReadRepresentation = new ConsentReadRepresentation();
        consentReadRepresentation.setConsentRequestDate("2020-01-01");
        Mockito.doThrow(new BadRequestException("error", referenceNumber)).when(service).revokeConsent("a", "b", "c");
        Assertions.assertThrows(BadRequestException.class, () -> resource.revokeConsent("a", "b", "c"));
    }

    @Test
    void addConsent() throws URISyntaxException {
        ConsentWriteRepresentation writeRepresentation = ConsentWriteRepresentation.builder().minor(UserWriteRepresentation.builder().identifierType("ID").identifierValue("123").countryOfIssue("IN").build()).build();
        Mockito.doNothing().when(service).addConsent(writeRepresentation);
        Mockito.when(uriInfo.getAbsolutePathBuilder()).thenReturn(uriBuilder);
        Mockito.when(uriBuilder.queryParam(Mockito.anyString(), Mockito.anyString())).thenReturn(uriBuilder);
        Mockito.when(uriBuilder.queryParam(Mockito.anyString(), Mockito.anyString())).thenReturn(uriBuilder);
        Mockito.when(uriBuilder.queryParam(Mockito.anyString(), Mockito.anyString())).thenReturn(uriBuilder);
        URI uri = new URI("http://localhost");
        Mockito.when(uriBuilder.build()).thenReturn(uri);

        Response response = resource.addConsent(uriInfo, writeRepresentation);

        Mockito.verify(service).addConsent(Mockito.any(ConsentWriteRepresentation.class));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(201, response.getStatus());
        Object entity = response.getEntity();
        Assertions.assertNull(entity);
        Assertions.assertEquals("http://localhost", response.getLocation().toString());
    }
}