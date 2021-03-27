//package tum.ret.rity.minor.consent.secure;
//
//import org.eclipse.microprofile.metrics.MetricUnits;
//import org.eclipse.microprofile.metrics.annotation.Counted;
//import org.eclipse.microprofile.metrics.annotation.Metered;
//import org.eclipse.microprofile.metrics.annotation.Timed;
//import org.eclipse.microprofile.openapi.annotations.Operation;
//import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
//import org.eclipse.microprofile.openapi.annotations.headers.Header;
//import org.eclipse.microprofile.openapi.annotations.links.Link;
//import org.eclipse.microprofile.openapi.annotations.media.Content;
//import org.eclipse.microprofile.openapi.annotations.media.Schema;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
//import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
//import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
//import tum.ret.rity.minor.consent.infrastructure.annotation.ScopesAllowed;
//import tum.ret.rity.minor.consent.representation.Body;
//import tum.ret.rity.minor.consent.representation.User;
//
//import javax.enterprise.context.RequestScoped;
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//
//@Path("/protected")
//@RequestScoped
//@Produces(MediaType.APPLICATION_JSON)
//public class ProtectedController {
//
//    @POST
//    @ScopesAllowed({"consent-admin"})
//    @Path("/{username}")
//    @Operation(summary = "Create user", description = "Detailed info", operationId = "createUser")
//    @APIResponses({
//            @APIResponse(
//                    description = "The user",
//                    responseCode = "200",
//                    headers = @Header(name = "x-custom-header", description = "custom header", required = true, schema = @Schema(type = SchemaType.STRING)),
//                    links = @Link(name = "relation", description = "some link", operationId = "getJWTBasedValue"),
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
//                    name = "Success response"
//            ),
//            @APIResponse(
//                    description = "Some error description",
//                    responseCode = "400",
//                    headers = @Header(name = "x-custom-header2", description = "custom header", required = true, schema = @Schema(type = SchemaType.STRING)),
//                    links = @Link(name = "relation2", description = "some link", operationId = "getJWTBasedValue"),
//                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)),
//                    name = "Error response"
//            )
//    })
//    @Consumes(MediaType.APPLICATION_JSON)
//    public User getJWTBasedValue2(
//            @RequestBody(
//                    description = "Request body payload",
//                    required = true,
//                    content = @Content(schema = @Schema(implementation = Body.class))) Body body,
//            @QueryParam("place") String place,
//            @PathParam("username") String username) {
//        return new User("I am accessible too");
//    }
//
//    @GET
//    @ScopesAllowed({"consent-admin"})
//    @Counted(name = "getJWTBasedValueInvocationCount", absolute = true, reusable = true, tags = {"tag1=value1"})
//    @Timed(name = "getJWTBasedValue_timed", absolute = true, description = "Time needed to complete the method",
//            unit = MetricUnits.MICROSECONDS)
//    @Metered(name = "getJWTBasedValue_metered", description = "Number of invocations of the getJWTBasedValue resource")
//    public String getJWTBasedValue() {
//        return "I am accessible";
//    }
//}
