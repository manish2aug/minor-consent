package tum.ret.rity.minor.consent.infrastructure.filter;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.jwt.JsonWebToken;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Log
@Provider
@Priority(ApplicationConstants.FILTER_PRIORITY_200)
public class AuditingFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jsonWebToken;
    @Context
    HttpHeaders headers;
    private static final Logger LOG = Logger.getLogger(AuditingFilter.class.getName());

    @SneakyThrows
    @Override
    public void filter(ContainerRequestContext requestContext) {
        log.warning(() -> String.format("%s|%s|%s", headers.getHeaderString(ApplicationConstants.REQUEST_ID), getAuditMessage(requestContext), getAuditableAttributesMap(requestContext)));
    }

    private String getAuditMessage(ContainerRequestContext requestContext) {
        UriInfo uriInfo = requestContext.getUriInfo();
        URI requestUri = uriInfo.getRequestUri();
        String queryString = requestUri.getQuery();
        queryString = (StringUtils.isNotBlank(queryString)) ? String.join("", "?", queryString) : "";
        String path = requestUri.getPath();
        String method = requestContext.getMethod();
        StringBuilder sb = new StringBuilder(method).append(": ")
                .append(path)
                .append(queryString);
        return sb.toString();
    }


    private Map<String, String> getAuditableAttributesMap(ContainerRequestContext requestContext) {
        Map<String, String> auditableAttributesMap = new HashMap<>();
        if (jsonWebToken != null) {
            String preferredUsername = jsonWebToken.getClaim("preferredUsername");
            if (StringUtils.isNotBlank(preferredUsername)) {
                auditableAttributesMap.put("user-in-token", preferredUsername);
            }
        } else {
            auditableAttributesMap.put("user-in-token", "N/A");
        }
        requestContext.getHeaders().forEach((a, b) -> auditableAttributesMap.put(a, String.join(",", b)));
        return auditableAttributesMap;
    }

}
