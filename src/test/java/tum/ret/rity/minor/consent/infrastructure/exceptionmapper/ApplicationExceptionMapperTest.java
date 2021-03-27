package tum.ret.rity.minor.consent.infrastructure.exceptionmapper;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.infrastructure.exception.BadRequestException;

import javax.ws.rs.core.Response;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationExceptionMapperTest {

    private String referenceNumber = "asasasas";
    private String errorMsg1 = "abcd";
    private String errorMsg2 = "efgh";

    @Test
    void toResponse() {
        Response response = new ApplicationExceptionMapper().toResponse(new BadRequestException(errorMsg1, referenceNumber));
        assertEquals(400, response.getStatus());
        assertNotNull(response.getEntity());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals(1, errorResponse.getErrorMsg().size());
        assertTrue(errorResponse.getErrorMsg().contains(errorMsg1));
        assertEquals(referenceNumber, errorResponse.getReferenceNumber());

        response = new ApplicationExceptionMapper().toResponse(new BadRequestException(Arrays.asList(errorMsg1, errorMsg2), referenceNumber));
        assertEquals(400, response.getStatus());
        assertNotNull(response.getEntity());
        errorResponse = (ErrorResponse) response.getEntity();
        assertEquals(2, errorResponse.getErrorMsg().size());
        assertTrue(errorResponse.getErrorMsg().contains(errorMsg1));
        assertTrue(errorResponse.getErrorMsg().contains(errorMsg2));
        assertEquals(referenceNumber, errorResponse.getReferenceNumber());

    }
}