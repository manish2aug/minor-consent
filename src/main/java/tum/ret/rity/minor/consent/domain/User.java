package tum.ret.rity.minor.consent.domain;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class User {
    private IdentifierTypeEnum identifierType;
    private String identifierValue;
    private String countryOfIssue;
}
