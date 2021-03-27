package tum.ret.rity.minor.consent.rest.representation;

import lombok.*;
import tum.ret.rity.minor.consent.domain.Consent;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ConsentReadRepresentation {

    @JsonbProperty("consent_request_date")
    private String consentRequestDate;
    @JsonbProperty("consent_applicable_date")
    private String consentApplicableDate;
    @JsonbProperty("consent_withdrawn_date")
    private String consentWithdrawnDate;
    private UserReadRepresentation guardian;
    private UserReadRepresentation minor;
    @JsonbProperty("birth_date")
    private String birthDate;
    @JsonbProperty("originating_system")
    private String originatingSystem;

    public static ConsentReadRepresentation getReadRepresentation(Consent consent) {
        if (consent == null)
            return null;
        return ConsentReadRepresentation
                .builder()
                .consentRequestDate(consent.getConsentRequestDateAsString())
                .consentApplicableDate(consent.getConsentApplicableDateAsString())
                .consentWithdrawnDate(consent.getConsentWithdrawnDateAsString())
                .guardian(UserReadRepresentation.getReadRepresentation(consent.getGuardian()))
                .minor(UserReadRepresentation.getReadRepresentation(consent.getMinor()))
                .birthDate(consent.getBirthDateAsString())
                .originatingSystem(consent.getOriginatingSystem())
                .build();

    }

    public static Collection<ConsentReadRepresentation> getReadRepresentation(Collection<Consent> allConsent) {
        if (allConsent == null || allConsent.isEmpty())
            return Collections.emptyList();
        return allConsent
                .stream()
                .map(ConsentReadRepresentation::getReadRepresentation)
                .collect(Collectors.toList());
    }
}
