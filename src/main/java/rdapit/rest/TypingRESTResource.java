package rdapit.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rdapit.pitservice.TypingService;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * This is the main class for REST web service interaction. The class offers
 * basic methods to create, read and delete PID records. Advanced methods
 * include to read individual properties, read property and type definition
 * information or check conformance to types.
 * 
 * All methods return JSON-encoded responses if not explicitly stated otherwise.
 * 
 * <h3>Example calls</h3>
 * 
 * The following calls assume that the REST service is deployed at the base URI
 * <u>http://localhost/pitapi</u>. <br/>
 * 
 * A simple test to check whether the REST service is running can be performed
 * by calling the {@link #simplePing ping} method:
 * 
 * <pre>
 * # curl http://localhost/pitapi/ping
 * Hello World
 * </pre>
 * 
 * <h4>Query properties of a pid</h4>
 * 
 * To query all properties, simply call {@link #resolvePID /pid}. Notice that you must encode all forward slashes in the PID name (replace with %2F).<br/>
 * An unsuccessful request will return a 404: 
 * 
 * <pre>
 * # curl http://localhost/pitapi/pid/1234%2F5678
 * ...
 * &lt; HTTP/1.1 404 Not Found
 * ...
 * Identifier not registered 
 * </pre>
 * 
 * A successful request may look like this:
 * <pre>
 * # curl http://localhost/pitapi/pid/11043.4
 * {"11314.2/2f305c8320611911a9926bb58dfad8c9":"CC-BY","URL":"http://www.example.com"} 
 * </pre>
 * 
 */
@Path("pitapi")
public class TypingRESTResource {

	protected TypingService typingService;

	public TypingRESTResource() {
		super();
		this.typingService = ApplicationContext.getInstance().getTypingService();
	}

	/**
	 * Simple ping method for testing (check whether the API is running etc.).
	 * Not part of the official interface description.
	 * 
	 * @return responds with 200 OK and a "Hello World" message in the body.
	 */
	@GET
	@Path("/ping")
	public Response simplePing() {
		return Response.status(200).entity("Hello World").build();
	}

	/**
	 * Generic resolution method to read PID records, property or type
	 * definitions. Not part of the official interface description.
	 * 
	 * @param identifier
	 *            an identifier string
	 * @return depending on the nature of the identified entity, the result can
	 *         be a PID record, a property or a type definition.
	 * @throws IOException
	 */
	@GET
	@Path("/generic/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveGenericPID(@PathParam("identifier") String identifier) throws IOException {
		Object obj = typingService.genericResolve(identifier);
		if (obj == null)
			return Response.status(404).build();
		return Response.status(200).entity(obj).build();
	}

	/**
	 * Simple HEAD method to check whether a particular pid is registered.
	 * 
	 * @param identifier
	 *            an identifier string
	 * @return either 200 or 404, indicating whether the PID is registered or
	 *         not registered
	 * @throws IOException
	 */
	@HEAD
	@Path("/pid/{identifier}")
	public Response isPidRegistered(@PathParam("identifier") String identifier) throws IOException {
		boolean b = typingService.isIdentifierRegistered(identifier);
		if (b)
			return Response.status(200).build();
		else
			return Response.status(404).build();
	}

	/**
	 * Sophisticated GET method to return all or some properties of an
	 * identifier.
	 * 
	 * @param identifier
	 * @param propertyNameOrID
	 *            Optional. Cannot be used in combination with the type
	 *            parameter. If given, the method returns only the value of the
	 *            single property. Note that this can be either a property name
	 *            or a property identifier. If it is a property name, the method
	 *            will consult a type registry to retrieve the corresponding
	 *            identifier. If this request does not provide a unique answer
	 *            (multiple properties with same name), the method will return a
	 *            412 (Precondition failed). If the name is not known in the
	 *            registry, the method will return a 404. The method will also
	 *            return 404 if a property identifier was given, the PID exists
	 *            but does not carry the given property.
	 * @param typeIdentifier
	 *            Optional. Cannot be used in combination with the property
	 *            parameter. If given, the method will return all properties
	 *            (mandatory and optional) that are specified in the given type
	 *            and listed in the identifier's record. The type parameter must
	 *            be a type identifier available from the registry. If the
	 *            identifier is not known in the registry, the method will
	 *            return 404.
	 * @return if the request is processed properly, the method will return 200
	 *         OK and a map of strings to strings from property identifiers (not
	 *         names!) to values. The method will return 404 if the identifier
	 *         is not known.
	 * @throws IOException
	 */
	@GET
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolvePID(@PathParam("identifier") String identifier, @QueryParam("property") @DefaultValue("") String propertyNameOrID,
			@QueryParam("type") @DefaultValue("") String typeIdentifier) throws IOException {
		if (!typeIdentifier.isEmpty()) {
			// Filter by type ID
			if (!propertyNameOrID.isEmpty())
				return Response.status(400).entity("Filtering by both type and property is not supported!").build();
			Map<String, String> result = typingService.queryByType(identifier, typeIdentifier);
			if (result == null)
				return Response.status(404).entity("Type not registered in the registry").build();
			return Response.status(200).entity(result).build();
		} else if (propertyNameOrID.isEmpty()) {
			// No filtering - return all properties
			Map<String, String> result = typingService.queryAllProperties(identifier);
			if (result == null)
				return Response.status(404).entity("Identifier not registered").build();
			return Response.status(200).entity(result).build();
		} else {
			// Filter by property name or ID
			try {
				String result = typingService.queryProperty(identifier, propertyNameOrID);
				if (result == null)
					return Response.status(404).entity("Property not present in identifier record").build();
				return Response.status(200).entity(result).build();
			} catch (IllegalArgumentException exc) {
				return Response.status(412).entity(exc.getMessage()).build();
			}
		}
	}

	/**
	 * GET method to read the definition of a property from the type registry.
	 * 
	 * @param identifier
	 *            the property identifier
	 * @return a property definition record or 404 if the property is unknown.
	 * @throws IOException
	 */
	@GET
	@Path("/property/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveProperty(@PathParam("identifier") String identifier) throws IOException {
		PropertyDefinition propDef = typingService.describeProperty(identifier);
		if (propDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(propDef).build();
	}

	/**
	 * GET method to read the definition of a type from the type registry.
	 * 
	 * @param identifier
	 *            the type identifier
	 * @return a type definition record or 404 if the type is unknown.
	 * @throws IOException
	 */
	@GET
	@Path("/type/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response resolveType(@PathParam("identifier") String identifier) throws IOException {
		TypeDefinition typeDef = typingService.describeType(identifier);
		if (typeDef == null)
			return Response.status(404).build();
		return Response.status(200).entity(typeDef).build();
	}

	/**
	 * GET method to check whether a given identifier conforms to a given type,
	 * i.e. carries all mandatory properties of the type.
	 * 
	 * @param identifier
	 *            the identifier name
	 * @param typeIdentifier
	 *            the type identifier
	 * @return a simple JSON record with a single boolean stating the
	 *         conformance result
	 */
	@GET
	@Path("/conformance/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response conformanceQuery(@PathParam("identifier") String identifier, @QueryParam("type") String typeIdentifier) {
		try {
			boolean b = typingService.conformsToType(identifier, typeIdentifier);
			return Response.status(200).entity(b).build();
		} catch (IllegalArgumentException exc) {
			return Response.status(400).entity(exc.getMessage()).build();
		} catch (IOException exc) {
			return Response.status(500).entity("Communication failure to type registry or identifier system: " + exc.getMessage()).build();
		}
	}

	/**
	 * Generic POST method to create new identifiers. The method determines an
	 * identifier name automatically, based on a purely random (version 4) UUID.
	 * 
	 * @param properties
	 *            a map from string to string, mapping property identifiers to
	 *            values.
	 * @return a simple string with the newly created PID name.
	 */
	@POST
	@Path("/pid")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response registerPID(Map<String, String> properties) {
		try {
			String pid = typingService.registerPID(properties);
			return Response.status(201).entity(pid).build();
		} catch (IOException exc) {
			return Response.status(500).entity("Communication failure to identifier system: " + exc.getMessage()).build();
		}
	}

	/**
	 * DELETE method to delete identifiers. Testing purposes only! Not part of
	 * the official specification.
	 * 
	 * @param identifier
	 * @return 200 or 404
	 */
	@DELETE
	@Path("/pid/{identifier}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletePID(@PathParam("identifier") String identifier) {
		boolean b = typingService.deletePID(identifier);
		if (b) {
			// This is not strictly necessary, but we just do it as a courtesy
			// (additional information to the user)
			Map<String, String> result = new HashMap<>();
			result.put(identifier, "deleted");
			return Response.status(200).entity(result).build();
		} else
			return Response.status(404).build();
	}
}
