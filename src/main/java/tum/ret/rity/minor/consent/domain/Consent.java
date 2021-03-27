package tum.ret.rity.minor.consent.domain;

import lombok.*;
import tum.ret.rity.minor.consent.persistence.dto.ConsentDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode
public class Consent {
    private LocalDate consentApplicableDate;
    private LocalDateTime consentWithdrawnDate;
    private LocalDateTime consentRequestDate;
    private User guardian;
    private User minor;
    private LocalDate birthDate;
    private String originatingSystem;

    public ConsentDTO getConsentDto() {
        return ConsentDTO.builder()
                .minorDateOfBirth(birthDate)
                .minorIdentifierType(minor.getIdentifierType().name())
                .minorIdentifierValue(minor.getIdentifierValue())
                .minorIdentifierIssuingCountry(minor.getCountryOfIssue())
                .guardianIdentifierType(guardian.getIdentifierType().name())
                .guardianIdentifierValue(guardian.getIdentifierValue())
                .guardianIdentifierIssuingCountry(guardian.getCountryOfIssue())
                .consentApplicableDate(consentApplicableDate)
                .originatingSystem(originatingSystem)
                .build();
    }

    public String getBirthDateAsString() {
        if (birthDate != null)
            return birthDate.toString();
        return null;
    }

    public String getConsentWithdrawnDateAsString() {
        if (consentWithdrawnDate != null)
            return consentWithdrawnDate.toString();
        return null;
    }

    public String getConsentApplicableDateAsString() {
        if (consentApplicableDate != null)
            return consentApplicableDate.toString();
        return null;
    }

    public String getConsentRequestDateAsString() {
        if (consentRequestDate != null)
            return consentRequestDate.toString();
        return null;
    }
}


