package tum.ret.rity.minor.consent.rest.representation;

import lombok.*;
import tum.ret.rity.minor.consent.domain.User;

import javax.json.bind.annotation.JsonbProperty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserReadRepresentation {

    @JsonbProperty("identifier_type")
    private String identifierType;
    @JsonbProperty("identifier_value")
    private String identifierValue;
    @JsonbProperty("identifier_issuing_country")
    private String countryOfIssue;

    public static UserReadRepresentation getReadRepresentation(User user) {
        if (user == null)
            return null;
        return UserReadRepresentation
                .builder()
                .identifierType(user.getIdentifierType().name())
                .identifierValue(user.getIdentifierValue())
                .countryOfIssue(user.getCountryOfIssue())
                .build();
    }
}