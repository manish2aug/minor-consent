package tum.ret.rity.minor.consent.secure;

import tum.ret.rity.minor.consent.infrastructure.ScopesAllowed;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/protected")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
public class ProtectedController {

    @GET
    @ScopesAllowed({"consent-admin"})
    public String getJWTBasedValue() {
        return "I am accessible";
    }
}
