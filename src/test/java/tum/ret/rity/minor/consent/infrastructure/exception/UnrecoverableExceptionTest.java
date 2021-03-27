package tum.ret.rity.minor.consent.infrastructure.exception;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnrecoverableExceptionTest {

    @Test
    void test() {
        UnrecoverableException exception = new UnrecoverableException("122333");
        ErrorResponse res = exception.getErrorResponse();
        assertTrue(res.getErrorMsg().contains(ApplicationConstants.GENERIC_EXCEPTION_MSG));
        assertEquals("122333", res.getReferenceNumber());
        assertEquals(ApplicationConstants.GENERIC_EXCEPTION_MSG, exception.getMessage());
        assertEquals(ApplicationConstants.GENERIC_EXCEPTION_MSG, exception.getLocalizedMessage());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, exception.getResponseStatus());
    }
}