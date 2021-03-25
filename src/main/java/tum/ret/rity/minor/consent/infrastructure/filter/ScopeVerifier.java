package tum.ret.rity.minor.consent.infrastructure.filter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.JsonWebToken;
import tum.ret.rity.minor.consent.infrastructure.ScopesAllowed;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Priority(100)
public class ScopeVerifier implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(ScopeVerifier.class.getName());
    @Inject
    JsonWebToken jwtToken;
    @Context
    private ResourceInfo resourceInfo;
    @Inject
    @Claim("scope")
    private ClaimValue<String> scopeClaim;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        validateRequest(requestContext);
    }

    private void validateRequest(ContainerRequestContext requestContext) {
        // omitted for bravity


        //1. Verify JWT token
        if (Objects.isNull(jwtToken) || StringUtils.isBlank(jwtToken.getSubject())) {
            log.log(Level.SEVERE, String.format("Token verification failure"));
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        log.log(Level.INFO, String.format("Token validated"));

        //2. Verify minimum claims in JWT token
        String[] requiredClaims = {"typ", "iss", "sub", "aud", "exp", "iat", "jti", "azp"};
        String requiredClaimsString = StringUtils.join(requiredClaims, ", ");
        if (!requiredClaimsExist(jwtToken, requiredClaims)) {
            log.log(Level.SEVERE,
                    String.format(
                            "Missing minimum required claims (%s), available claims in token: %s",
                            requiredClaimsString,
                            jwtToken.getClaimNames()));
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("NOT_AUTHORIZED").build());
            return;
        }
        log.log(Level.INFO, String.format("Minimum required claims (%s) available in token", requiredClaimsString));

        //3. Verify requested operation specific requirement of claims
        String[] allowedScopes = getAllowedScopes();
        String allowedScopesString = StringUtils.join(allowedScopes, ", ");
        if (ArrayUtils.isEmpty(allowedScopes)) {
            log.log(Level.INFO, String.format("No scope restriction for requested operation found"));
            return;
        }
        log.log(Level.INFO, String.format("Scope restriction found for the requested operation, allowed scopes: %s", allowedScopesString));

        //4. Verify operation specific claims availability
        if (!jwtToken.containsClaim("scope") || Objects.isNull(scopeClaim) || StringUtils.isBlank(scopeClaim.getValue())) {
            log.log(Level.SEVERE, String.format("Scope claim not available"));
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        log.log(Level.INFO, String.format("Claim ('scope') found in token"));

        //5. Verify requested operation specific claim value presence in token
        String availableScopesInJwtToken = scopeClaim.getValue();
        if (Arrays.stream(allowedScopes).noneMatch(availableScopesInJwtToken::contains)) {
            log.log(Level.SEVERE,
                    String.format(
                            "Required scope (%s) NOT found in token",
                            allowedScopesString));
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).entity("NOT_AUTHORIZED").build());
            return;
        }
        log.log(Level.INFO, String.format("Required scope (%s) for the requested operation found in token", allowedScopesString));
    }

    private boolean requiredClaimsExist(JsonWebToken jwtToken, String... claims) {
        return Arrays.stream(claims).allMatch(jwtToken::containsClaim);
    }

    private String[] getAllowedScopes() {
        String[] allowedScopes = extractAllowedScopes(resourceInfo.getResourceMethod());
        if (ArrayUtils.isEmpty(allowedScopes)) {
            allowedScopes = extractAllowedScopes(resourceInfo.getResourceClass());
        }
        return allowedScopes;
    }

    private String[] extractAllowedScopes(final AnnotatedElement annotatedElement) {
        String[] scopes = {};
        if (annotatedElement != null && annotatedElement.isAnnotationPresent(ScopesAllowed.class)) {
            scopes = annotatedElement.getAnnotation(ScopesAllowed.class).value();
        }
        return scopes;
    }
}
