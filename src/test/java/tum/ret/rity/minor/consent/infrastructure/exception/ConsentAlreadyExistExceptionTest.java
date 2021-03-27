package tum.ret.rity.minor.consent.infrastructure.exception;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsentAlreadyExistExceptionTest {

    @Test
    void test() {
        ConsentAlreadyExistException exception = new ConsentAlreadyExistException("122333");
        ErrorResponse res = exception.getErrorResponse();
        assertTrue(res.getErrorMsg().contains("Consent already exist for the minor in request"));
        assertEquals("122333", res.getReferenceNumber());
        assertEquals(Response.Status.CONFLICT, exception.getResponseStatus());
    }
}