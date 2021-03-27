package tum.ret.rity.minor.consent.repository;

import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.infrastructure.exception.ConsentAlreadyExistException;
import tum.ret.rity.minor.consent.infrastructure.exception.NoConsentFoundException;
import tum.ret.rity.minor.consent.infrastructure.exception.UnrecoverableException;

import java.time.LocalDate;
import java.util.Collection;

public interface ConsentRepository {

    /**
     * Add a consent for minor
     *
     * @param consent the consent payload
     * @return
     * @throws UnrecoverableException
     * @throws ConsentAlreadyExistException
     */
    int addMinorConsent(Consent consent) throws UnrecoverableException, ConsentAlreadyExistException;

    /**
     * Retrieve existing consent for the minor if exists
     *
     * @param identifierType  the type of identifier of minor (ID or PASSPORT)
     * @param identifierValue the value of identifier of minor
     * @param issuingCountry  the 2 characters ISO country code issues the identifier
     * @return {@link Consent}
     * @throws NoConsentFoundException if no consent found with supplied criteria
     */
    Consent getConsent(String identifierType, String identifierValue, String issuingCountry) throws NoConsentFoundException;

    /**
     * Get all consents (Applicable and withdrawn) for the supplied search criteria
     *
     * @param fromDate         the start date from (inclusive) which the consents were applicable
     * @param toDate           the start date till (inclusive) which the consents were applicable
     * @param includeWithdrawn a flag to include the the withdrawn consents
     * @return List of all {@link Consent}
     */
    Collection<Consent> getAllConsent(LocalDate fromDate, LocalDate toDate, boolean includeWithdrawn);

    /**
     * Revoke the consent for the minor if exists
     *
     * @param identifierType  the type of identifier of minor (ID or PASSPORT)
     * @param identifierValue the value of identifier of minor
     * @param issuingCountry  the 2 characters ISO country code issues the identifier
     * @throws NoConsentFoundException if no consent found with supplied criteria
     */
    void revokeConsent(String identifierType, String identifierValue, String issuingCountry) throws NoConsentFoundException;

}
