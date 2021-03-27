package tum.ret.rity.minor.consent.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class BadRequestException extends ApplicationException {

    private final Collection<String> messages = new ArrayList<>();
    private final String referenceNumber;

    public BadRequestException(String msg, String referenceNumber) {
        this.referenceNumber = referenceNumber;
        if (StringUtils.isNotBlank(msg))
            this.messages.add(msg);
    }

    public BadRequestException(Collection<String> msg, String referenceNumber) {
        this.referenceNumber = referenceNumber;
        if (msg != null && !msg.isEmpty())
            this.messages.addAll(msg);
    }

    public ErrorResponse getErrorResponse() {
        return new ErrorResponse(messages, referenceNumber);
    }

    public Response.Status getResponseStatus() {
        return Response.Status.BAD_REQUEST;
    }
}
