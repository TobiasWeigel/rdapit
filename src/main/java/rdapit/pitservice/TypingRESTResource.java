package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

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

	@HEAD
	@Path("/pid/{identifier}")
	public Response isPidRegistered(@PathParam("identifier") String identifier) throws IOException {
		boolean b = typingService.isIdentifierRegistered(identifier);
		if (b)
			return Response.status(200).build();
		else
			return Response.status(404).build();
	}

	@GET
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolvePID(@PathParam("identifier") String identifier, @QueryParam("property") @DefaultValue("") String propertyNameOrID, @QueryParam("type") @DefaultValue("") String typeIdentifier)
			throws IOException {
		if (!typeIdentifier.isEmpty()) {
			// Filter by type ID
			if (!propertyNameOrID.isEmpty())
				return Response.status(400).entity("Filtering by both type and property is not supported!").build();
			typingService.queryByType(identifier, typeIdentifier);
		}
		if (propertyNameOrID.isEmpty()) {
			// No filtering - return all properties
			Map<String, String> result = typingService.queryAllProperties(identifier);
			if (result == null)
				return Response.status(404).build();
			return Response.status(200).entity(result).build();
		} else {
			// Filter by property name or ID
			String result = typingService.queryProperty(identifier, propertyNameOrID);
			if (result == null)
				return Response.status(404).build();
			return Response.status(200).entity(result).build();
		}
	}

	@GET
	@Path("/property/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveProperty(@PathParam("identifier") String identifier) throws IOException {
		PropertyDefinition propDef = typingService.describeProperty(identifier);
		if (propDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(propDef).build();
	}

	@GET
	@Path("/type/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveType(@PathParam("identifier") String identifier) throws IOException {
		TypeDefinition typeDef = typingService.describeType(identifier);
		if (typeDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(typeDef).build();
	}

}
