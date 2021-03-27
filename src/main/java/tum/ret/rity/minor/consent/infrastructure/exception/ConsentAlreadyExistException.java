package tum.ret.rity.minor.consent.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Setter
@Getter
public class ConsentAlreadyExistException extends ApplicationException {
    private final String referenceNumber;

    public ConsentAlreadyExistException(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(Collections.singletonList("Consent already exist for the minor in request"), referenceNumber);
    }

    public Response.Status getResponseStatus() {
        return Response.Status.CONFLICT;
    }
}
