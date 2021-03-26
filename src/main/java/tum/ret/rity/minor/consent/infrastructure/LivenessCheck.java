package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    /**
     * Invokes the health check procedure provided by the implementation of this interface.
     *
     * @return {@link HealthCheckResponse} object containing information about the health check result
     */
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("live-ness Check")
                .withData("key", "value")
                .down()
                .build();
    }
}
