//package tum.ret.rity.minor.consent.infrastructure;
//
//import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
//import org.eclipse.microprofile.openapi.annotations.info.Info;
//import org.eclipse.microprofile.openapi.annotations.servers.Server;
//import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;
//import org.eclipse.microprofile.openapi.annotations.tags.Tag;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.Response;
//
//@OpenAPIDefinition(
//        info = @Info(title = "Sample API", version = "1.0.0"),
//        tags = @Tag(name = "admin", description = "Admin capabilities"),
//        servers = {
//                @Server(
//                        description = "definition server 1",
//                        url = "http://{var1}.definition1/{var2}",
//                        variables = {
//                                @ServerVariable(name = "var1",
//                                        description = "var 1",
//                                        defaultValue = "1",
//                                        enumeration = {"1", "2"}),
//                                @ServerVariable(name = "var2",
//                                        description = "var 2",
//                                        defaultValue = "1",
//                                        enumeration = {"1", "2"})}),
//                @Server(
//                        description = "class server 1",
//                        url = "http://{var1}.class1/{var2}",
//                        variables = {
//                                @ServerVariable(
//                                        name = "var1",
//                                        description = "var 1",
//                                        defaultValue = "1",
//                                        enumeration = {"1", "2"}),
//                                @ServerVariable(
//                                        name = "var2",
//                                        description = "var 2",
//                                        defaultValue = "1",
//                                        enumeration = {"1", "2"})}),
//                @Server(
//                        description = "class server 2",
//                        url = "http://{var1}.class2",
//                        variables = {
//                                @ServerVariable(
//                                        name = "var1",
//                                        description = "var 1",
//                                        defaultValue = "1",
//                                        enumeration = {"1", "2"})})
//        })
//public class ServersFilter {
//
//    @GET
//    @Path("/")
//    @Server(
//            description = "method server 1",
//            url = "http://{var1}.method1",
//            variables = {
//                    @ServerVariable(
//                            name = "var1",
//                            description = "var 1",
//                            defaultValue = "1",
//                            enumeration = {"1", "2"})})
//    @Server(
//            description = "method server 2",
//            url = "http://method2"
//    )
//    public Response getServers() {
//        return Response.ok().entity("ok").build();
//    }
//}