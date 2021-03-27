package tum.ret.rity.minor.consent.persistence.dto;

import lombok.*;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class ConsentDTO {
    private String minorIdentifierType;
    private String minorIdentifierValue;
    private String minorIdentifierIssuingCountry;
    private LocalDate minorDateOfBirth;
    private String guardianIdentifierType;
    private String guardianIdentifierValue;
    private String guardianIdentifierIssuingCountry;
    private LocalDateTime consentRequestDate;
    private LocalDate consentApplicableDate;
    private String originatingSystem;
    private LocalDateTime consentWithdrawnDate;

    public Consent getDomainObject() {
        return Consent.builder()
                .consentApplicableDate(consentApplicableDate)
                .consentWithdrawnDate(consentWithdrawnDate)
                .consentRequestDate(consentRequestDate)
                .guardian(User.builder()
                        .identifierType(IdentifierTypeEnum.fromString(guardianIdentifierType))
                        .identifierValue(guardianIdentifierValue)
                        .countryOfIssue(guardianIdentifierIssuingCountry)
                        .build())
                .minor(User.builder()
                        .identifierType(IdentifierTypeEnum.fromString(minorIdentifierType))
                        .identifierValue(minorIdentifierValue)
                        .countryOfIssue(minorIdentifierIssuingCountry)
                        .build())
                .birthDate(minorDateOfBirth)
                .originatingSystem(originatingSystem)
                .build();
    }
}
