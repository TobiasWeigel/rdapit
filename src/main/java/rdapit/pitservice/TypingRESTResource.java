package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.pitservice.TypingService;

@Path("/pitapi")
public class TypingRESTResource {

	protected TypingService typingService;
	
	public TypingRESTResource() {
		super();
		this.typingService = ApplicationContext.getInstance().getTypingService();
	}
	
	
	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String simpleTest() {
		return "Hello World!";
	}

	@GET
	@Path("/generic/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveGenericPID(@PathParam("identifier") String identifier) throws IOException {
		Object obj = typingService.genericResolve(identifier);
		if (obj == null)
			return Response.status(404).build();
		return Response.status(200).entity(obj).build();
	}
	
	@GET
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolvePID(@PathParam("identifier") String identifier) throws IOException {
		Map<String, String> result = typingService.getAllProperties(identifier);
		if (result == null)
			return Response.status(404).build();
		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/property/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveProperty(@PathParam("identifier") String identifier) throws IOException {
		System.out.println("identifier: "+identifier);
		return Response.status(200).entity(identifier).build();
	}
	
	/*
	@GET
	@Path("/property")
	@Produces(MediaType.TEXT_PLAIN)
	public Response resolveProperty() throws IOException {
		String identifier = "hhh";
		System.out.println("Querying property "+identifier);
		PropertyDefinition result = typingService.describeProperty(identifier);
		if (result == null)
			return Response.status(404).build();
		return Response.status(200).entity(result).build();
		
	} */
	

}
