package tum.ret.rity.minor.consent.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Setter
@Getter
public class ForbiddenException extends ApplicationException {

    private final String msg;
    private final String referenceNumber;

    public ForbiddenException(String msg, String referenceNumber) {
        this.msg = msg;
        this.referenceNumber = referenceNumber;
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(Collections.singletonList(msg), referenceNumber);
    }

    public Response.Status getResponseStatus() {
        return Response.Status.FORBIDDEN;
    }
}
