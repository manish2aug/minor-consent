package tum.ret.rity.minor.consent.infrastructure.exceptionmapper;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    @Test
    void test() {
        assertAll(
                () -> assertTrue(new ErrorResponse(null, "123").getErrorMsg().isEmpty()),
                () -> assertEquals(1, new ErrorResponse(Collections.singleton("abcd"), "123").getErrorMsg().size()),
                () -> assertTrue(new ErrorResponse(Collections.emptyList(), "123").getErrorMsg().isEmpty())
        );
    }

}