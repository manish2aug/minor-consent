package tum.ret.rity.minor.consent.persistence.mappers;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.domain.IdentifierTypeEnum;
import tum.ret.rity.minor.consent.persistence.dto.ConsentDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * <p>This test runs against the actual postgres database on dev environment, make sure the machine on which this
 * test is run, can access the DEV db</p>
 *
 * @author manish2aug
 */
public class ConsentMapperTest {

    private static SqlSessionFactory sessionFactory;

    @BeforeAll
    static void beforeAll() {
        PooledDataSource pooledDataSource = getTestDatasource();
        Configuration config = new Configuration(new Environment("dev", new ManagedTransactionFactory(), pooledDataSource));
        config.getVariables().put("SCHEMA_NAME", ApplicationConstants.SCHEMA_NAME);
        config.addMapper(ConsentMapper.class);

        if (ApplicationConstants.ENABLE_MYBATIS_LOGGING) {
            config.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        }
        sessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    private static PooledDataSource getTestDatasource() {
        String url = StringUtils.join("jdbc:postgresql://", ApplicationConstants.DB_TESTING_TEST_PG_SERVER, ":5432/", ApplicationConstants.DB_TESTING_TEST_PG_DB);
        PooledDataSource pooledDataSource = new PooledDataSource("org.postgresql.Driver", url, ApplicationConstants.DB_TESTING_TEST_PG_USERNAME, "password");
        return pooledDataSource;
    }

    @Test
    void test_success_flow() {
        try (SqlSession session = sessionFactory.openSession()) {
            ConsentMapper mapper = session.getMapper(ConsentMapper.class);
            ConsentDTO dto = getTestConsentDto();
            // Add a consent and verify the operation completes successfully
            int insertedRows = mapper.insert(dto);
            Assertions.assertEquals(1, insertedRows);
            // retrieve & verify the specific consent
            ConsentDTO select = mapper.select(dto.getMinorIdentifierType(), dto.getMinorIdentifierValue(), dto.getMinorIdentifierIssuingCountry());
            Assertions.assertEquals("J6025323-test", select.getMinorIdentifierValue());
            // retrieve all existing consent
            Collection<ConsentDTO> consentDTOS = mapper.selectAll(LocalDate.of(2021, 12, 31), LocalDate.of(2021, 12, 31));
            Assertions.assertTrue(consentDTOS.stream().anyMatch(a -> StringUtils.equals(a.getMinorIdentifierValue(), "J6025323-test")));
            // remove the consent the operation completes successfully
            int deletedRows = mapper.delete(dto.getMinorIdentifierType(), dto.getMinorIdentifierValue(), dto.getMinorIdentifierIssuingCountry());
            Assertions.assertEquals(1, deletedRows);
            // retrieve all withdrawn and existing consents
            consentDTOS = mapper.selectAll(LocalDate.of(2021, 12, 31), LocalDate.of(2021, 12, 31));
            Assertions.assertFalse(consentDTOS.stream().anyMatch(a -> StringUtils.equals(a.getMinorIdentifierValue(), "J6025323-test")));
            consentDTOS = mapper.selectAllWithdrawn(LocalDate.of(2021, 12, 31), LocalDate.of(2021, 12, 31));
            Assertions.assertTrue(consentDTOS.stream().anyMatch(a -> StringUtils.equals(a.getMinorIdentifierValue(), "J6025323-test")));
        }
    }

    private ConsentDTO getTestConsentDto() {
        return ConsentDTO.builder()
                .consentApplicableDate(LocalDate.of(2021, 12, 31))
                .consentRequestDate(LocalDateTime.now())
                .guardianIdentifierType(IdentifierTypeEnum.ID.name())
                .guardianIdentifierValue("2112319606084")
                .guardianIdentifierIssuingCountry("ZA")
                .minorIdentifierType(IdentifierTypeEnum.PASSPORT.name())
                .minorIdentifierValue("J6025323-test")
                .minorIdentifierIssuingCountry("IN")
                .minorDateOfBirth(LocalDate.of(2002, 01, 01))
                .originatingSystem("originatingSystem")
                .build();
    }

}