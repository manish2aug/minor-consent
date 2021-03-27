package tum.ret.rity.minor.consent.infrastructure.exception;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoConsentFoundExceptionTest {

    @Test
    void test() {
        NoConsentFoundException exception = new NoConsentFoundException("122333");
        ErrorResponse res = exception.getErrorResponse();
        assertEquals("122333", res.getReferenceNumber());
        assertEquals(Response.Status.NOT_FOUND, exception.getResponseStatus());
    }

}