package tum.ret.rity.minor.consent;

import org.microshed.testing.SharedContainerConfiguration;
import org.microshed.testing.testcontainers.ApplicationContainer;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.util.HashMap;
import java.util.Map;

public class AppDeploymentConfig implements SharedContainerConfiguration {

    @Container
    public static ApplicationContainer app = new ApplicationContainer()
            .withHttpPort(9082)
            .withAppContextRoot("/minor-consent")
            .withReadinessPath("/health/ready");


//    @Container
//    static GenericContainer<?> keycloak =
//            new GenericContainer("jboss/keycloak:11.0.0")
//                    .waitingFor(Wait.forHttp("/auth").forStatusCode(200))
//                    .withExposedPorts(8080)
////                    .withClasspathResourceMapping("/keycloak/dump.json", "/tmp/dump.json", BindMode.READ_ONLY)
//                    .withEnv(getMap());
//
//    private static Map getMap() {
//        Map map = new HashMap();
//        map.put("KEYCLOAK_USER", "testcontainers");
//        map.put("KEYCLOAK_PASSWORD", "testcontainers");
//        map.put("JAVA_OPTS", "-D ... -Dkeycloak.migration.file=/tmp/dump.json");
//        map.put("DB_VENDOR", "h2");
//        return map;
//    }
//
//    @Container
//    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:12")
//            .withUsername("testcontainers")
//            .withPassword("testcontainers")
//            .withInitScript("database/INIT.sql") // inside src/test/resources
//            .withDatabaseName("tescontainers");
}