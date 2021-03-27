package tum.ret.rity.minor.consent.infrastructure;

import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.mybatis.cdi.SessionFactoryProvider;
import tum.ret.rity.minor.consent.constants.ApplicationConstants;
import tum.ret.rity.minor.consent.persistence.mappers.ConsentMapper;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.util.logging.Level;

@Log
@Setter
@ApplicationScoped
public class ApplicationProducers {

    private SqlSessionFactory sqlSessionFactory;

    @Resource(name = ApplicationConstants.DATA_SOURCE)
    private DataSource dataSource;

    @Produces
    @ApplicationScoped
    @SessionFactoryProvider
    public SqlSessionFactory produceSqlSessionFactory() {
        try {
            if (sqlSessionFactory == null) {
                Configuration config = new Configuration(new Environment("default", new ManagedTransactionFactory(), dataSource));
                config.getVariables().put("SCHEMA_NAME", ApplicationConstants.SCHEMA_NAME);
                config.addMapper(ConsentMapper.class);

                if (ApplicationConstants.ENABLE_MYBATIS_LOGGING) {
                    config.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
                }
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(config);
            }

            return sqlSessionFactory;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unable to get SQL session factory", e);
            return null;
        }
    }
}
