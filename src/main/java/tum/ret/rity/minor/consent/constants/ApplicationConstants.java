package tum.ret.rity.minor.consent.constants;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class ApplicationConstants {

    public static final String CLIENT_SECRET = "6ffab308-6096-4cff-ba48-4c6995115498";

    public static final String SOUTH_AFRICA_COUNTRY_CODE = "ZA";
    public static final String MINOR_EFFECTIVE_BIRTH_DATE_INVALID = "Minor's dateOfBirth is invalid";

    private ApplicationConstants() {
        // not to be initialized
    }


    public static final int FILTER_PRIORITY_100 = 100;
    public static final int FILTER_PRIORITY_200 = 200;
    public static final boolean ENABLE_MYBATIS_LOGGING = true;
    public static final String DATA_SOURCE = "jdbc/consentApiDs";
    public static final String SCHEMA_NAME = "mrt_consent_schema";
    public static final String VALID_DATE_REGEX = "^(?:(?:(?:(?:(?:[1-9]\\d)(?:0[48]|[2468][048]|[13579][26])|(?:(?:[2468][048]|[13579][26])00))(-)(?:0?2\\1(?:29)))|(?:(?:[1-9]\\d{3})(-)(?:(?:(?:0?[13578]|1[02])\\2(?:31))|(?:(?:0?[13-9]|1[0-2])\\2(?:29|30))|(?:(?:0?[1-9])|(?:1[0-2]))\\2(?:0?[1-9]|1\\d|2[0-8])))))$";
    public static final String VALID_IDENTIFIER_TYPE_REGEX = "ID|PASSPORT";

    /* ---------- messages------------------ */
    public static final String DATA_INTEGRITY_FAILURE = "Data integrity failure, please refer the logs for more details";
    public static final String INVALID_COUNTRY_FOR_ID = "Invalid identifier_issuing_country, allowed country for ID is ZA";
    public static final String IDENTIFIER_TYPE_VALIDATION_ERROR_MSG = "identifier_type is mandatory and allowed values are ID & PASSPORT";
    public static final String IDENTIFIER_VALUE_LENGTH_VALIDATION_ERROR_MSG = "Allowed length for identifier_value is between 1 and 50";
    public static final String IDENTIFIER_ISSUING_COUNTRY_VALIDATION_ERROR_MSG = "identifier_issuing_country is mandatory and must be a valid ISO 3166-1 alpha-2 code";
    public static final String MINOR_EFFECTIVE_BIRTH_DATE_VALIDATION_ERROR_MSG = "Minor's effective birth date is invalid";
    public static final String MINOR_EFFECTIVE_BIRTH_DATE_VALIDATION_ERROR_MSG_2 = "As per effective birthDate of minor, age criteria (< 18) doesn't match";
    public static final String GUARDIAN_ID_VALIDATION_ERROR_MSG_2 = "Supplied ID number for guardian, doesn't satisfy age criteria (> 18)";
    public static final String MINOR_BIRTH_DATE_VALIDATION_ERROR_MSG_2 = "Minor's birth date (birth_date) is invalid, must be a valid past date in yyyy-MM-dd format and should qualify for a minor (age < 18)";
    public static final String MINOR_ID_NUMBER_VALIDATION_ERROR_MSG = "Supplied ID number for minor is not valid";
    public static final String IDENTIFIER_VALUE_VALIDATION_ERROR_MSG = "identifier_value is mandatory";

    public static final String NOT_AUTHORIZED_ERROR_MSG = "Not authorized to perform the requested operation";
    public static final String ID_VALIDATION_ERROR_MSG = "Supplied ID number is not valid";
    public static final String CONSENT_APPLICABLE_DATE_VALIDATION_ERROR_MSG = "consent_applicable_date is invalid, value must be a valid future or present date in yyyy-MM-dd format";

    public static final String INVALID_SEARCH_CRITERIA_ERROR_MSG = "identifier_type, identifier_value and identifier_issuing_country are mandatory for lookup\\delete a consent";
    public static final String INVALID_REPORT_CRITERIA_ERROR_MSG = "from_date & to_date both are mandatory for report retrieval";
    public static final String INVALID_DATE_RANGE_CRITERIA_ERROR_MSG = "from_date should be smaller or equals to the to_date";
    public static final String INVALID_REPORT_FLAG_ERROR_MSG = "Allowed values for include_withdrawn are true or false";
    public static final String GENERIC_EXCEPTION_MSG = "Unable to process request, please refer the logs with help of referenceNumber";

    public static final String ID_REGEX = "(((\\d{2}((0[13578]|1[02])(0[1-9]|[12]\\d|3[01])|(0[13456789]|1[012])(0[1-9]|[12]\\d|30)|02(0[1-9]|1\\d|2[0-8])))|([02468][048]|[13579][26])0229))(\\d{7})";
    public static final String INSERT_CONSENT_SQL = "INSERT INTO minor_consent_schema.minor_consent "
            + "VALUES ("
            + "#{dto.minorIdentifierType, jdbcType=VARCHAR}, "
            + "#{dto.minorIdentifierValue, jdbcType=VARCHAR}, "
            + "#{dto.minorIdentifierIssuingCountry, jdbcType=VARCHAR}, "
            + "#{dto.minorDateOfBirth, jdbcType=DATE}, "
            + "#{dto.guardianIdentifierType, jdbcType=VARCHAR}, "
            + "#{dto.guardianIdentifierValue, jdbcType=VARCHAR}, "
            + "#{dto.guardianIdentifierIssuingCountry, jdbcType=VARCHAR}, "
            + "CURRENT_TIMESTAMP, "
            + "#{dto.consentApplicableDate, jdbcType=DATE}, "
            + "#{dto.originatingSystem, jdbcType=VARCHAR})";
    private static final String FOR_READ_ONLY = "FOR READ ONLY";
    public static final String SELECT_CONSENT_SQL = "SELECT * FROM minor_consent_schema.minor_consent WHERE "
            + "minor_identifier_type = #{identifierType, jdbcType=VARCHAR} "
            + "AND minor_identifier_value = #{identifierValue, jdbcType=VARCHAR} "
            + "AND minor_identifier_issuing_country = #{issuingCountry, jdbcType=VARCHAR} "
            + FOR_READ_ONLY;
    public static final String SELECT_ALL_CONSENT_SQL = "SELECT * FROM minor_consent_schema.minor_consent WHERE "
            + "consent_applicable_date >= #{fromDate, jdbcType=DATE} "
            + "AND consent_applicable_date <= #{toDate, jdbcType=DATE} "
            + FOR_READ_ONLY;
    public static final String SELECT_ALL_WITHDRAWN_CONSENT_SQL = "SELECT * FROM minor_consent_schema.minor_consent_withdrawn_history WHERE "
            + "consent_applicable_date >= #{fromDate, jdbcType=DATE} "
            + "AND consent_applicable_date <= #{toDate, jdbcType=DATE} "
            + FOR_READ_ONLY;
    public static final String DELETE_CONSENT_SQL = "DELETE FROM minor_consent_schema.minor_consent WHERE "
            + "minor_identifier_type = #{identifierType, jdbcType=VARCHAR} "
            + "AND minor_identifier_value = #{identifierValue, jdbcType=VARCHAR} "
            + "AND minor_identifier_issuing_country = #{issuingCountry, jdbcType=VARCHAR}";

    public static final String REQUEST_ID = "referenceNumber";
    public static final String DB_TESTING_TEST_PG_USERNAME = "postgres";
    public static final String DB_TESTING_TEST_PG_SERVER = "localhost";
    public static final String DB_TESTING_TEST_PG_DB = "MRT_CONSENT";
    public static final String INVALID_FROM_DATE_ERROR_MSG = "from_date is invalid";
    public static final String INVALID_TO_DATE_ERROR_MSG = "to_date is invalid";


    //-------------------------------- Utility methods ------------------------------------
    @SneakyThrows
    public static URI rewriteUrlSafely(String url) {
        if (StringUtils.isNotBlank(url)) {
            url = url.replace("internal-", "");
            return new URI(url);
        }
        return null;
    }

    public static LocalDate getLocalDate(String callerDateOfBirth) {
        LocalDate convertedDate = null;
        if (StringUtils.isNotBlank(callerDateOfBirth)) {
            DateTimeFormatter dtf2 = DateTimeFormatter.ISO_DATE;
            DateTimeFormatter dtf3 = DateTimeFormatter.ISO_LOCAL_DATE;
            DateTimeFormatter dtf4 = DateTimeFormatter.BASIC_ISO_DATE;

            convertedDate = getLocalDate(callerDateOfBirth, dtf2);
            if (convertedDate == null) convertedDate = getLocalDate(callerDateOfBirth, dtf3);
            if (convertedDate == null) convertedDate = getLocalDate(callerDateOfBirth, dtf4);
        }
        return convertedDate;
    }

    public static LocalDate getLocalDate(String dateString, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(dateString, formatter.withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isMinor(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears() < 18;
    }

    public static boolean isMinor(String callerDateOfBirth) {
        LocalDate localDate = getLocalDate(callerDateOfBirth);
        if (localDate == null)
            return false;
        return Period.between(localDate, LocalDate.now()).getYears() < 18;
    }

    public static boolean isMinorAsPerIdNumber(String idNumber) {
        LocalDate localDate = getDateOfBirthFromID(idNumber);
        if (localDate == null)
            return false;
        return Period.between(localDate, LocalDate.now()).getYears() < 18;
    }

    public static LocalDate getDateOfBirthFromID(String idNumber) {
        if (isValidIdNumber(idNumber)) {
            int year = Integer.parseInt(idNumber.substring(0, 2));
            int currentYear = LocalDate.now().getYear() % 100;
            int century = 19;

            if (year < currentYear)
                century = 20;

            String month = idNumber.substring(2, 4);
            String day = idNumber.substring(4, 6);
            return LocalDate.of((century * 100) + year, Integer.parseInt(month), Integer.parseInt(day));
        }
        return null;
    }

    @SneakyThrows
    public static boolean isValidBirthDateAsPerID(String idNumber, String birthDate) {
        LocalDate localBirthDate = getLocalDate(birthDate);
        if (isValidIdNumber(idNumber) && localBirthDate != null) {
            LocalDate birthDateFromID = getDateOfBirthFromID(idNumber);
            return birthDateFromID != null && localBirthDate.compareTo(birthDateFromID) == 0;
        }
        return false;
    }

    public static boolean isValidIdNumber(String idNumber) {
        if (!isMatchingPattern(idNumber, ApplicationConstants.ID_REGEX))
            return false;

        char[] idChars = idNumber.toCharArray();
        int sum = 0;
        // Loop over each digit right-to-left, including the check-digit
        for (int i = 1; i <= idChars.length; i++) {
            int digit = Character.getNumericValue(idChars[idChars.length - i]);
            if ((i % 2) != 0) {
                sum += digit;
            } else {
                sum += digit < 5 ? digit * 2 : digit * 2 - 9;
            }
        }

        return (sum % 10) == 0;
    }

    public static boolean isMatchingPattern(String toBeMatched, String pattern) {
        return StringUtils.isNotBlank(toBeMatched) && java.util.regex.Pattern.compile(pattern).matcher(toBeMatched).matches();
    }


}
