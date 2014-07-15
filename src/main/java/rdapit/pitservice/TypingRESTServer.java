package rdapit.pitservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.pidsystem.DummyIdentifierSystem;
import rdapit.pidsystem.IIdentifierSystem;
import rdapit.typeregistry.DummyTypeRegistry;
import rdapit.typeregistry.ITypeRegistry;

@Path("/pitapi")
public class TypingRESTServer {

	protected ITypingService typingService;

	public TypingRESTServer() {
		super();
		IIdentifierSystem identifierSystem = new DummyIdentifierSystem();
		ITypeRegistry typeRegistry = new DummyTypeRegistry();
		this.typingService = new TypingService(identifierSystem, typeRegistry);
	}

	@GET
	@Path("/resolve")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resolvePID(String identifier) {
		String result = "Hello world!";
		return Response.status(200).entity(result).build();
	}

}
