package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
    /**
     * Invokes the health check procedure provided by the implementation of this interface.
     *
     * @return {@link HealthCheckResponse} object containing information about the health check result
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("readiness Check")
                .withData("key1", "value1")
                .up()
                .build();
    }
}
