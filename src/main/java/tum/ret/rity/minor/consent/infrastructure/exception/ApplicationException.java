package tum.ret.rity.minor.consent.infrastructure.exception;

import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;

public abstract class ApplicationException extends Exception {
    public abstract ErrorResponse getErrorResponse();

    public abstract Response.Status getResponseStatus();
}
