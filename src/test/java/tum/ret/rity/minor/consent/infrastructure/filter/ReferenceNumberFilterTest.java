package tum.ret.rity.minor.consent.infrastructure.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

class ReferenceNumberFilterTest {

    @Mock
    ContainerRequestContext requestContext;
    @InjectMocks
    ReferenceNumberFilter filter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void filter() {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();
        Mockito.when(requestContext.getHeaders()).thenReturn(map);
        filter.filter(requestContext);
        MultivaluedMap<String, String> headers = requestContext.getHeaders();
        Assertions.assertFalse(requestContext.getHeaders().getFirst("referenceNumber").isEmpty());
    }
}