package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/minor-consent/api")
@LoginConfig(authMethod = "MP-JWT", realmName = "test")
public class MinorConsentRestApplication extends Application {
}
