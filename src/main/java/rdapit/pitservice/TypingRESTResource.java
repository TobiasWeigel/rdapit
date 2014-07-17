package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;

@Path("/pitapi")
public class TypingRESTResource {

	protected ITypingService typingService;

	public TypingRESTResource() throws IOException {
		super();
		// TODO: need to replace dummies with proper instances - where do we get their config info?
		IIdentifierSystem identifierSystem = ApplicationContext.getInstance().getIdentifierSystem();
		ITypeRegistry typeRegistry = ApplicationContext.getInstance().getTypeRegistry();
		this.typingService = new TypingService(identifierSystem, typeRegistry);
	}

	@GET
	@Path("generic/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveGenericPID(@PathParam("identifier") String identifier) throws IOException {
		Object obj = typingService.genericResolve(identifier);
		if (obj == null)
			return Response.status(404).build();
		return Response.status(200).entity(obj).build();
	}
	
	@GET
	@Path("pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolvePID(@PathParam("identifier") String identifier) throws IOException {
		Map<String, String> result = typingService.getAllProperties(identifier);
		if (result == null)
			return Response.status(404).build();
		return Response.status(200).entity(result).build();
	}
	
	@GET
	@Path("property/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveProperty(@PathParam("identifier") String identifier) throws IOException {
		PropertyDefinition result = typingService.describeProperty(identifier);
		if (result == null)
			return Response.status(404).build();
		return Response.status(200).entity(result).build();
		
	}
	

}
