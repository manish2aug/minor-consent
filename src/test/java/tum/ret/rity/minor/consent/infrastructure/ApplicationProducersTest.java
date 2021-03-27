package tum.ret.rity.minor.consent.infrastructure;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;

class ApplicationProducersTest {

    @Mock
    DataSource dataSource;
    @InjectMocks
    ApplicationProducers producers;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void produceSqlSessionFactory() {
        DefaultSqlSessionFactory aDefault = new DefaultSqlSessionFactory(
                new Configuration(
                        new Environment(
                                "default",
                                new ManagedTransactionFactory(),
                                dataSource)));
        producers.setSqlSessionFactory(aDefault);
        Assertions.assertEquals(aDefault, producers.produceSqlSessionFactory());
    }

    @Test
    void produceSqlSessionFactory_createNew() {
        producers.setSqlSessionFactory(null);
        Assertions.assertNotNull(producers.produceSqlSessionFactory());
    }

}