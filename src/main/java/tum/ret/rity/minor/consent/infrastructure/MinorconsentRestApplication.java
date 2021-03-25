package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/data")
@LoginConfig(authMethod = "MP-JWT", realmName = "test")
public class MinorconsentRestApplication extends Application {
}
