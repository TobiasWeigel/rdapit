package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.pidsystem.DummyIdentifierSystem;
import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pidsystem.PID;
import rdapit.typeregistry.DummyTypeRegistry;
import rdapit.typeregistry.ITypeRegistry;

@Path("/pitapi")
public class TypingRESTResource {

	protected ITypingService typingService;

	public TypingRESTResource() throws IOException {
		super();
		IIdentifierSystem identifierSystem = new DummyIdentifierSystem();
		ITypeRegistry typeRegistry = new DummyTypeRegistry();
		this.typingService = new TypingService(identifierSystem, typeRegistry);
	}

	@GET
	@Path("/generic/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resolveGenericPID(@PathParam("identifier") String identifier) throws IOException {
		Object obj = typingService.genericResolve(new PID(identifier));
		if (obj == null)
			return Response.status(404).build();
		return Response.status(200).entity(obj).build();
	}
	
	@GET
	@Path("/pid/{identifier}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resolvePID(@PathParam("identifier") String identifier) throws IOException {
		Map<String, String> result = typingService.getAllProperties(new PID(identifier));
		if (result == null)
			return Response.status(404).build();
		return Response.status(200).entity(result).build();
	}
	

}
