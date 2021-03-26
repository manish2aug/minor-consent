package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class CombinedCheck {

    @Produces
    @Liveness
    HealthCheck check1() {
        return () -> HealthCheckResponse.named("heap-memory")
                .withData("memory-usage", String.valueOf(getMemUsage()))
                .state(getMemUsage() < 0.9)
                .build();
    }

    private double getMemUsage() {
        return 1.8;
    }

    @Produces
    @Readiness
    HealthCheck check2() {
        return () -> HealthCheckResponse.named("cpu-usage")
                .state(getCpuUsage() < 0.9)
                .withData("memory-usage", String.valueOf(getMemUsage()))
                .build();
    }

    private double getCpuUsage() {
        return 0.6;
    }
}