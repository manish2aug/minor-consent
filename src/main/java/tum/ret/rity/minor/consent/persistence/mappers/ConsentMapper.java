package tum.ret.rity.minor.consent.persistence.mappers;

import org.apache.ibatis.annotations.*;
import org.mybatis.cdi.Mapper;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.persistence.dto.ConsentDTO;

import java.time.LocalDate;
import java.util.Collection;


@Mapper
public interface ConsentMapper {

    /**
     * Insert a consent for minor in database
     *
     * @param consentDTO the consent object
     */
    @Insert(ApplicationConstants.INSERT_CONSENT_SQL)
    int insert(@Param("dto") ConsentDTO consentDTO);

    @Results(id = "consent", value = {
            @Result(property = "minorIdentifierType", column = "minor_identifier_type"),
            @Result(property = "minorIdentifierValue", column = "minor_identifier_value"),
            @Result(property = "minorIdentifierIssuingCountry", column = "minor_identifier_issuing_country"),
            @Result(property = "minorDateOfBirth", column = "minor_birth_date"),
            @Result(property = "guardianIdentifierType", column = "guardian_identifier_type"),
            @Result(property = "guardianIdentifierValue", column = "guardian_identifier_value"),
            @Result(property = "guardianIdentifierIssuingCountry", column = "guardian_identifier_issuing_country"),
            @Result(property = "consentRequestDate", column = "consent_request_date"),
            @Result(property = "consentApplicableDate", column = "consent_applicable_date"),
            @Result(property = "originatingSystem", column = "originating_system")
    })
    @Select(ApplicationConstants.SELECT_CONSENT_SQL)
    ConsentDTO select(
            @Param("identifierType") String identifierType,
            @Param("identifierValue") String identifierValue,
            @Param("issuingCountry") String issuingCountry);

    /**
     * Delete the consent
     *
     * @param identifierType  type of identifier
     * @param identifierValue value of the identifier
     * @param issuingCountry  the issuing country for identifier
     * @return number of rows deleted
     */
    @Delete(ApplicationConstants.DELETE_CONSENT_SQL)
    int delete(@Param("identifierType") String identifierType,
               @Param("identifierValue") String identifierValue,
               @Param("issuingCountry") String issuingCountry);

    /**
     * Returns the collection of existing consents based on teh supplied criteria
     *
     * @param fromDate the date from which (including) the given consents to be included
     * @param toDate   the date till which (including) the given consents to be included
     * @return List of consents
     */
    @Results(id = "all-consent", value = {
            @Result(property = "minorIdentifierType", column = "minor_identifier_type"),
            @Result(property = "minorIdentifierValue", column = "minor_identifier_value"),
            @Result(property = "minorIdentifierIssuingCountry", column = "minor_identifier_issuing_country"),
            @Result(property = "minorDateOfBirth", column = "minor_birth_date"),
            @Result(property = "guardianIdentifierType", column = "guardian_identifier_type"),
            @Result(property = "guardianIdentifierValue", column = "guardian_identifier_value"),
            @Result(property = "guardianIdentifierIssuingCountry", column = "guardian_identifier_issuing_country"),
            @Result(property = "consentRequestDate", column = "consent_request_date"),
            @Result(property = "consentApplicableDate", column = "consent_applicable_date"),
            @Result(property = "originatingSystem", column = "originating_system")
    })
    @Select(ApplicationConstants.SELECT_ALL_CONSENT_SQL)
    Collection<ConsentDTO> selectAll(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Returns the collection of existing and revoked consents based on teh supplied criteria
     *
     * @param fromDate the date from which (including) the given consents to be included
     * @param toDate   the date till which (including) the given consents to be included
     * @return List of consents
     */
    @Results(id = "all-withdrawn-consent", value = {
            @Result(property = "minorIdentifierType", column = "minor_identifier_type"),
            @Result(property = "minorIdentifierValue", column = "minor_identifier_value"),
            @Result(property = "minorIdentifierIssuingCountry", column = "minor_identifier_issuing_country"),
            @Result(property = "minorDateOfBirth", column = "minor_birth_date"),
            @Result(property = "guardianIdentifierType", column = "guardian_identifier_type"),
            @Result(property = "guardianIdentifierValue", column = "guardian_identifier_value"),
            @Result(property = "guardianIdentifierIssuingCountry", column = "guardian_identifier_issuing_country"),
            @Result(property = "consentRequestDate", column = "consent_request_date"),
            @Result(property = "consentApplicableDate", column = "consent_applicable_date"),
            @Result(property = "consentWithdrawnDate", column = "consent_withdrawn_date"),
            @Result(property = "originatingSystem", column = "originating_system")
    })
    @Select(ApplicationConstants.SELECT_ALL_WITHDRAWN_CONSENT_SQL)
    Collection<ConsentDTO> selectAllWithdrawn(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}

