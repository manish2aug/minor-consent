package tum.ret.rity.minor.consent.infrastructure;

import org.eclipse.microprofile.auth.LoginConfig;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.*;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/data")
@LoginConfig(authMethod = "MP-JWT", realmName = "test")
@OpenAPIDefinition(
        info = @Info(title = "Sample API", description = "REST-ful API", version = "1.0.1", contact = @Contact(name = "Manish", email = "manish2aug@gmail.com")),
        tags = @Tag(name = "Admin", description = "Admin capabilities"),
        security = {@SecurityRequirement(name = "api-sec", scopes = {"consent", "consent-admin"})},
        components = @Components(
                securitySchemes = @SecurityScheme(
                        securitySchemeName = "api-sec",
                        type = SecuritySchemeType.OAUTH2,
                        flows = @OAuthFlows(
                                authorizationCode = @OAuthFlow(
                                        authorizationUrl = "https://localhost:8443/auth/realms/test/protocol/openid-connect/auth",
                                        tokenUrl = "https://localhost:8443/auth/realms/test/protocol/openid-connect/token",
                                        refreshUrl = "https://localhost:8443/auth/realms/test/protocol/openid-connect/token",
                                        scopes = {
                                                @OAuthScope(
                                                        name = "consent",
                                                        description = "Allows users to retrieve/add/delete the consents"),
                                                @OAuthScope(
                                                        name = "consent-admin",
                                                        description = "Allows admin users to perform all operations")}))
                )),
        servers = {
                @Server(description = "Local", url = "http://{host}:{port}",
                        variables = {
                                @ServerVariable(name = "host",
                                        description = "server's host",
                                        defaultValue = "localhost",
                                        enumeration = {"10.0.0.103", "10.0.0.104"}),
                                @ServerVariable(name = "port",
                                        description = "server's port",
                                        defaultValue = "9082",
                                        enumeration = {"9083", "9084"})}),
                @Server(description = "Proxy", url = "http://{internal-nginx-path}",
                        variables = {
                                @ServerVariable(name = "internal-nginx-path",
                                        description = "server's host",
                                        defaultValue = "internal-retail-dev",
                                        enumeration = {"internal-retail-tst", "internal-retail-pre"})
                        })
        })
public class MinorconsentRestApplication extends Application {
}
