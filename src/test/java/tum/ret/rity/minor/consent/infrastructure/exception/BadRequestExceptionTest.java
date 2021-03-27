package tum.ret.rity.minor.consent.infrastructure.exception;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.infrastructure.exceptionmapper.ErrorResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BadRequestExceptionTest {

    @Test
    void test() {
        BadRequestException exception = new BadRequestException("abcd", "122333");
        assertTrue(exception.getMessages().contains("abcd"));
        ErrorResponse res = exception.getErrorResponse();
        assertTrue(res.getErrorMsg().contains("abcd"));
        assertEquals("122333", res.getReferenceNumber());
        assertEquals(Response.Status.BAD_REQUEST, exception.getResponseStatus());
        exception = new BadRequestException(Collections.emptyList(), "122333");
        assertTrue(exception.getMessages().isEmpty());
        exception = new BadRequestException("", "122333");
        assertTrue(exception.getMessages().isEmpty());
    }
}