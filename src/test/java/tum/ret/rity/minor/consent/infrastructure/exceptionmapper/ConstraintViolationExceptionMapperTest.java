package tum.ret.rity.minor.consent.infrastructure.exceptionmapper;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ConstraintViolationExceptionMapperTest {

    private String referenceNumber = "asasasas";
    private String errorMsg1 = "abcd";
    private String errorMsg2 = "efgh";

    @Test
    void toResponse() {
        ConstraintViolation<Object> objectConstraintViolation = ConstraintViolationImpl.forBeanValidation(
                null,
                null,
                null,
                errorMsg1,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
        ConstraintViolationException exception = new ConstraintViolationException(Collections.singleton(objectConstraintViolation));
        Response response = new ConstraintViolationExceptionMapper().toResponse(exception);
        assertEquals(400, response.getStatus());
        assertNotNull(response.getEntity());
        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals(1, errorResponse.getErrorMsg().size());
        assertTrue(errorResponse.getErrorMsg().contains(errorMsg1));
        assertNull(errorResponse.getReferenceNumber());

    }
}