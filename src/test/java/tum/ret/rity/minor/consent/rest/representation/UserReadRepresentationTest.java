package tum.ret.rity.minor.consent.rest.representation;

import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserReadRepresentationTest {

    @Test
    public void getReadRepresentation() {
        assertNull(UserReadRepresentation.getReadRepresentation(null));
        assertNotNull(UserReadRepresentation.getReadRepresentation(new User(IdentifierTypeEnum.ID, "123123123", "ZA")));
    }
}