package tum.ret.rity.minor.consent.infrastructure.exception;

import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class NoConsentFoundException extends ApplicationException {
    private final String referenceNumber;

    public NoConsentFoundException(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(Collections.singletonList("No consent found"), referenceNumber);
    }

    public Response.Status getResponseStatus() {
        return Response.Status.NOT_FOUND;
    }
}
