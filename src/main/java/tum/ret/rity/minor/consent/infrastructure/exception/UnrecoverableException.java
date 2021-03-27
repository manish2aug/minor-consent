package tum.ret.rity.minor.consent.infrastructure.exception;

import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class UnrecoverableException extends ApplicationException {
    private final String referenceNumber;

    public UnrecoverableException(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(Collections.singletonList(ApplicationConstants.GENERIC_EXCEPTION_MSG), referenceNumber);
    }

    public Response.Status getResponseStatus() {
        return Response.Status.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return ApplicationConstants.GENERIC_EXCEPTION_MSG;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }
}
