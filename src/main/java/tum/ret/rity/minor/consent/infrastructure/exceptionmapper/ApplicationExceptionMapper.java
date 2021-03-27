package tum.ret.rity.minor.consent.infrastructure.exceptionmapper;

import tum.ret.rity.minor.consent.infrastructure.exception.ApplicationException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {

    @Override
    public Response toResponse(ApplicationException exception) {
        return Response
                .status(exception.getResponseStatus())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(exception.getErrorResponse())
                .build();
    }
}