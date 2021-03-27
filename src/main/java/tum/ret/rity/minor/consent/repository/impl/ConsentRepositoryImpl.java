package tum.ret.rity.minor.consent.repository.impl;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.Consent;
import tum.ret.rity.minor.consent.infrastructure.exception.ConsentAlreadyExistException;
import tum.ret.rity.minor.consent.infrastructure.exception.NoConsentFoundException;
import tum.ret.rity.minor.consent.infrastructure.exception.UnrecoverableException;
import tum.ret.rity.minor.consent.persistence.dto.ConsentDTO;
import tum.ret.rity.minor.consent.persistence.mappers.ConsentMapper;
import tum.ret.rity.minor.consent.repository.ConsentRepository;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.HeaderParam;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Log
@ApplicationScoped
public class ConsentRepositoryImpl implements ConsentRepository {

    @HeaderParam(ApplicationConstants.REQUEST_ID)
    String referenceNumber;
    @Inject
    private ConsentMapper mapper;
    @Resource(lookup = "concurrent/execSvc")
    ManagedExecutorService managedExecutorService;

    @Override
    @SneakyThrows({ConsentAlreadyExistException.class, UnrecoverableException.class})
    public int addMinorConsent(Consent consent) {
        try {
            int insertedRows = mapper.insert(consent.getConsentDto());
            if (insertedRows != 0)
                return insertedRows;
            log.log(Level.INFO,
                    String.format(
                            "%s| [DB] Consent creation successful, rows added: %s; for (consent: %s)",
                            referenceNumber,
                            insertedRows,
                            consent));
        } catch (PersistenceException exception) {
            log.log(Level.SEVERE,
                    String.format("%s| [DB] Consent creation failed", referenceNumber),
                    exception);
            if (StringUtils.contains(exception.getLocalizedMessage(), "already exists"))
                throw new ConsentAlreadyExistException(referenceNumber);
        }
        throw new UnrecoverableException(referenceNumber);
    }

    @SneakyThrows(NoConsentFoundException.class)
    @Override
    public Consent getConsent(
            String identifierType,
            String identifierValue,
            String issuingCountry) {

        ConsentDTO select = mapper.select(identifierType, identifierValue, issuingCountry);
        if (select == null) {
            log.severe(() -> String.format(
                    "%s| [DB] No consent found for identifier_type: %s, identifier_value: %s, identifier_issuing_country: %s",
                    referenceNumber, identifierType, identifierValue, issuingCountry));
            throw new NoConsentFoundException(referenceNumber);
        }
        log.log(Level.INFO,
                String.format(
                        "%s| [DB] Consent retrieval successful; for (identifierType: %s, identifierValue: %s, issuingCountry: %s)",
                        referenceNumber,
                        identifierType,
                        identifierValue,
                        issuingCountry));
        return select.getDomainObject();
    }

    @SneakyThrows(NoConsentFoundException.class)
    @Override
    public void revokeConsent(
            String identifierType,
            String identifierValue,
            String issuingCountry) {

        int deletedRows = mapper.delete(identifierType, identifierValue, issuingCountry);
        if (deletedRows == 0) {
            log.severe(() -> String.format(
                    "%s| [DB] No consent found in database for identifier_type: %s, identifier_value: %s, identifier_issuing_country: %s",
                    referenceNumber, identifierType, identifierValue, issuingCountry));
            throw new NoConsentFoundException(referenceNumber);
        }
        log.log(Level.INFO,
                String.format(
                        "%s| [DB] Consent removal successful, rows deleted: %s; for (identifierType: %s, identifierValue: %s, issuingCountry: %s)",
                        referenceNumber,
                        deletedRows,
                        identifierType,
                        identifierValue,
                        issuingCountry));
    }

    @Override
    public Collection<Consent> getAllConsent(
            LocalDate fromDate,
            LocalDate toDate,
            boolean includeWithdrawn) {

        Collection<Consent> consentCollection = new ArrayList<>();
        CompletableFuture<Void> selectAllConsentAsync =
                CompletableFuture.runAsync(() ->
                        mapper.selectAll(fromDate, toDate)
                                .stream()
                                .map(ConsentDTO::getDomainObject)
                                .forEach(consentCollection::add), managedExecutorService);

        CompletableFuture<Void> selectAllWithdrawnConsentAsync = (includeWithdrawn) ? CompletableFuture.runAsync(() ->
                mapper.selectAllWithdrawn(fromDate, toDate)
                        .stream()
                        .map(ConsentDTO::getDomainObject)
                        .forEach(consentCollection::add), managedExecutorService) : CompletableFuture.completedFuture(null);

        try {
            CompletableFuture.allOf(selectAllConsentAsync, selectAllWithdrawnConsentAsync).join();
        } catch (Exception exception) {
            // fail silently and return empty collection if both operation fails or just what could be retrieved for partial success
            log.log(Level.SEVERE,
                    String.format(
                            "%s| [DB] Consent report retrieval failed; for (fromDate: %s, toDate: %s, includeWithdrawn: %s)",
                            referenceNumber,
                            fromDate,
                            toDate,
                            includeWithdrawn),
                    exception);
        }
        log.log(Level.INFO,
                String.format(
                        "%s| [DB] Consent report retrieval successful, records found: %s; for (fromDate: %s, toDate: %s, includeWithdrawn: %s)",
                        referenceNumber,
                        consentCollection.size(),
                        fromDate,
                        toDate,
                        includeWithdrawn));
        return consentCollection;
    }
}
