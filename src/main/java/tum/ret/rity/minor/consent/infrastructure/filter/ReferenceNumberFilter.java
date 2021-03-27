package tum.ret.rity.minor.consent.infrastructure.filter;

import tum.ret.rity.minor.consent.constants.ApplicationConstants;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@Priority(ApplicationConstants.FILTER_PRIORITY_100)
@PreMatching
public class ReferenceNumberFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) {
        requestContext
                .getHeaders()
                .add(ApplicationConstants.REQUEST_ID, UUID.randomUUID().toString());
    }
}