package tum.ret.rity.minor.consent.infrastructure.filter;

import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tum.ret.rity.minor.consent.rest.representation.ConsentWriteRepresentation;
import tum.ret.rity.minor.consent.rest.resource.ConsentRestResource;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.logging.Logger;

class JwtTokenVerificationFilterTest {

    @Mock
    ContainerRequestContext requestContext;
    @Mock
    JsonWebToken jsonWebToken;
    @Mock
    ClaimValue<String> scope;
    @Mock
    AnnotatedElement annotatedElement;
    @Mock
    ResourceInfo resourceInfo;
    @Mock
    private Logger log;
    @InjectMocks
    ScopeVerifier filter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilter_nullScopeClaimOnlyClassHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn(null);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_blankScopeClaimOnlyClassHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("");
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_scopeMismatchOnlyClassHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("consent1");
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_validScopeClaimOnlyClassHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("consent");
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_nullScopeClaimMethodHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn(null);
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
        Mockito.verify(resourceInfo, Mockito.never()).getResourceClass();
    }

    @Test
    void testFilter_blankScopeClaimMethodHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
        Mockito.verify(resourceInfo, Mockito.never()).getResourceClass();
    }

    @Test
    void testFilter_scopeMismatchMethodHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("consent1");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
        Mockito.verify(resourceInfo, Mockito.never()).getResourceClass();
    }

    @Test
    void testFilter_validScopeClaimMethodHasAllowedScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.when(jsonWebToken.getClaim("scope")).thenReturn("consent-admin");
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent-admin");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
        Mockito.verify(resourceInfo, Mockito.never()).getResourceClass();
    }

    @Test
    void testFilter_noAvailableScopeInResource() throws NoSuchMethodException {
        Method getConsentMethod = this.getClass().getDeclaredMethod("testFilter_noAvailableScopeInResource");
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(this.getClass()).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        filter.filter(requestContext);
        Mockito.verify(jsonWebToken, Mockito.never()).getClaim(Mockito.any(String.class));
    }


    /*getConsent()*/
    @Test
    void testFilter_getConsentShouldWorkForConsentScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentShouldWorkForConsentAdminScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent-admin");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentShouldNotWorkForOtherScopeThanConsentAdminAndConsent() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("other");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentShouldNotWorkIfNoScopeClaimFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(false);
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentShouldNotWorkIfEmptyScopeFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    /*getConsentAll()*/
    @Test
    void testFilter_getConsentAllShouldNotWorkForConsentScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentAllShouldWorkForConsentAdminScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent-admin");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentAllShouldNotWorkForOtherScopeThanConsentAdminAndConsent() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("other");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentAllShouldNotWorkIfNoScopeClaimFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(false);
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_getConsentAllShouldNotWorkIfEmptyScopeFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("getAllConsents", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    /*revokeConsent()*/
    @Test
    void testFilter_revokeConsentShouldWorkForConsentScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("revokeConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_revokeConsentShouldWorkForConsentAdminScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("revokeConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent-admin");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_revokeConsentShouldNotWorkForOtherScopeThanConsentAdminAndConsent() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("revokeConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("other");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_revokeConsentShouldNotWorkIfNoScopeClaimFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("revokeConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(false);
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_revokeConsentShouldNotWorkIfEmptyScopeFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("revokeConsent", String.class, String.class, String.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    /*addConsent()*/
    @Test
    void testFilter_addConsentShouldWorkForConsentScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("addConsent", UriInfo.class, ConsentWriteRepresentation.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_addConsentShouldWorkForConsentAdminScope() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("addConsent", UriInfo.class, ConsentWriteRepresentation.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.getSubject()).thenReturn("something");
        Mockito.when(jsonWebToken.containsClaim(Mockito.anyString())).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("consent-admin");
        filter.filter(requestContext);
        Mockito.verify(requestContext, Mockito.never()).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_addConsentShouldNotWorkForOtherScopeThanConsentAdminAndConsent() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("addConsent", UriInfo.class, ConsentWriteRepresentation.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("other");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_addConsentShouldNotWorkIfNoScopeClaimFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("addConsent", UriInfo.class, ConsentWriteRepresentation.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(false);
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }

    @Test
    void testFilter_addConsentShouldNotWorkIfEmptyScopeFound() throws NoSuchMethodException {
        Method getConsentMethod = ConsentRestResource.class.getDeclaredMethod("addConsent", UriInfo.class, ConsentWriteRepresentation.class);
        Mockito.when(resourceInfo.getResourceMethod()).thenReturn(getConsentMethod);
        Mockito.doReturn(ConsentRestResource.class).when(resourceInfo).getResourceClass();
        Mockito.when(jsonWebToken.containsClaim("scope")).thenReturn(true);
        Mockito.when(scope.getValue()).thenReturn("");
        filter.filter(requestContext);
        Mockito.verify(requestContext).abortWith(Mockito.any(ResponseImpl.class));
    }


}