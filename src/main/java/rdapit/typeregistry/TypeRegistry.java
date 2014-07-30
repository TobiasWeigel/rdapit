package rdapit.typeregistry;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import rdapit.pitservice.EntityClass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeRegistry implements ITypeRegistry {

	protected final URI baseURI;
	protected String identifierPrefix;

	protected Client client;
	protected WebTarget rootTarget;
	protected WebTarget searchTarget;
	protected WebTarget idTarget;
	protected WebTarget createTarget;

	public TypeRegistry(String baseURI, String identifierPrefix) {
		this.baseURI = UriBuilder.fromUri(baseURI).build();
		this.identifierPrefix = identifierPrefix.trim();
		client = ClientBuilder.newBuilder().build();
		rootTarget = client.target(baseURI);
		searchTarget = rootTarget.path("search").path("DataType");
		idTarget = rootTarget.path("records").path("{id}");
		createTarget = rootTarget.path("model").path("DataType");
	}

	@Override
	public PropertyDefinition queryPropertyDefinition(String propertyIdentifier) throws IOException {
		String response = idTarget.resolveTemplate("id", propertyIdentifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		return constructPropertyDefinition(rootNode);
	}
	
	private PropertyDefinition constructPropertyDefinition(JsonNode rootNode) {
		JsonNode entry = rootNode.get("extras").get("data");
		String propName = entry.get("human_description").asText();
		String valuetype = "";
		String namespace = "";
		String description = entry.get("explanation_of_use").asText();
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				String key = entryKV.get("key").asText();
				if (key.equals(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY) && !entryKV.get("val").asText().equalsIgnoreCase("PROPERTY_DEFINITION"))
					// this is not a property record!
					return null;
				else if (key.equalsIgnoreCase("range"))
					valuetype = entryKV.get("val").asText();
				else if (key.equalsIgnoreCase("namespace"))
					namespace = entryKV.get("val").asText();
			}
		}
		return new PropertyDefinition(entry.get("ID").asText(), propName, valuetype, namespace, description);
	}

	@Override
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException {
		String response = searchTarget.queryParam("query", propertyName).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		List<PropertyDefinition> results = new ArrayList<PropertyDefinition>();
		if (rootNode.get("code").asInt() != 200)
			return results;
		for (JsonNode entry : rootNode.get("extras").get("data")) {
			if (!entry.get("model").asText().equals("DataType"))
				continue;
			// crawl through the key_value list of this type record to find the
			// 'name' and 'type' of the property
			String valuetype = "";
			String propertyNameFromRecord = "";
			boolean isPropDef = false;
			if (entry.get("human_description").asText().equalsIgnoreCase(propertyName))
				propertyNameFromRecord = entry.get("human_description").asText();
			if (entry.has("key_value")) {
				for (JsonNode entryKV : entry.get("key_value")) {
					if (entryKV.get("key").asText().equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY)) {
						if (entryKV.get("val").asText().equalsIgnoreCase("PROPERTY_DEFINITION"))
							isPropDef = true;
					}
					else if (entryKV.get("key").asText().equalsIgnoreCase("range"))
						valuetype = entryKV.get("val").asText();
				}
			}
			if (isPropDef && !valuetype.isEmpty() && !propertyNameFromRecord.isEmpty()) {
				// found both value type and original property name; add valid
				// property definition to result list
				results.add(new PropertyDefinition(entry.get("ID").asText(), propertyNameFromRecord, valuetype));
			}
		}
		return results;
	}

	public Response createPropertyDefinition(PropertyDefinition propertyDefinition, String creator, String emailAddress, Date date, String organization) throws IOException {
		/* Note: This approach is currently broken as the type registry prototype only accepts multipart/form data.
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("provenance.creator", creator);
		map.put("provenance.email_address", emailAddress);
		map.put("provenance.creation_date", new SimpleDateFormat("yyyyMMdd").format(date));
		map.put("provenance.organization", organization);
		map.put("human_description", propertyDefinition.getName());
		map.put("explanation_of_use", propertyDefinition.getDescription());
		Map<String, String> key_value = new HashMap<String, String>();
		key_value.put(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY, "PROPERTY_DEFINITION");
		key_value.put("range", propertyDefinition.getRange());
		key_value.put("namespace", propertyDefinition.getNamespace());
		map.put("key_value", key_value);
		Response resp = createTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(map));
		if (resp.getStatus() >= 400)
			throw new IOException("Communication error with the type registry, HTTP code "+resp.getStatus()+": "+resp.readEntity(String.class));
	 	return resp;  
	}
	
	private TypeDefinition constructTypeDefinition(JsonNode rootNode) throws JsonProcessingException, IOException {
		JsonNode entry = rootNode.get("extras").get("data");
		Map<String, Boolean> properties = new HashMap<>();
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				String key = entryKV.get("key").asText();
				if (key.equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY) && !entryKV.get("val").asText().equalsIgnoreCase("TYPE_DEFINITION"))
					// this is not a type record!
					return null;
				else if (key.equalsIgnoreCase("property")) {
					// This value is a small json snippet with the property ID
					// and a mandatory flag
					ObjectMapper mapper = new ObjectMapper();
					JsonNode propNode = mapper.readTree(entryKV.get("val").asText());
					properties.put(propNode.get("id").asText(), propNode.get("mandatory").asBoolean());
				}
			}
		}
		String typeUseExpl = entry.get("explanation_of_use").asText();
		String description = entry.get("human_description").asText();
		TypeDefinition result = new TypeDefinition(entry.get("ID").asText(), typeUseExpl, description);
		// add properties
		for (String pd : properties.keySet())
			result.addProperty(pd, properties.get(pd));
		return result;
		
	}

	@Override
	public TypeDefinition queryTypeDefinition(String typeIdentifier) throws IOException {
		String response = idTarget.resolveTemplate("id", typeIdentifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		return constructTypeDefinition(rootNode); 
	}

	@Override
	public void createTypeDefinition(String typeIdentifier, TypeDefinition typeDefinition) {
		throw new UnsupportedOperationException("not implemented yet");
		// TODO: also store PIT.Construct field, PIT.Version
	}

	@Override
	public void removePropertyDefinition(String propertyIdentifier) throws IOException {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Object query(String identifier) throws JsonProcessingException, IOException {
		String response = idTarget.resolveTemplate("id", identifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		EntityClass entityClass = determineEntityClass(rootNode);
		if (entityClass == null)
			return null;
		if (entityClass == EntityClass.PROPERTY)
			return constructPropertyDefinition(rootNode);
		if (entityClass == EntityClass.TYPE)
			return constructTypeDefinition(rootNode);
		throw new IllegalStateException("Invalid EntityClass enum value: "+entityClass);
	}
	
	private EntityClass determineEntityClass(JsonNode rootNode) {
		JsonNode entry = rootNode.get("extras").get("data");
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				if (entryKV.get("key").asText().equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY)) {
					String v = entryKV.get("val").asText();
					if (v.equalsIgnoreCase("PROPERTY_DEFINITION")) 
						return EntityClass.PROPERTY;
					if (v.equalsIgnoreCase("TYPE_DEFINITION")) 
						return EntityClass.TYPE;
					String id = "???";
					if (entry.get("ID") != null) id = entry.get("ID").asText();
					throw new IllegalStateException("Unknown value for "+PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY+" in record "+id+": "+v);
				}
			}
		}
		return null;
	}

	@Override
	public EntityClass determineEntityClass(String identifier) throws IOException {
		// retrieve full record and analyze marker field
		String response = idTarget.resolveTemplate("id", identifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		return determineEntityClass(rootNode);
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		return pid.startsWith(identifierPrefix);
	}
	
	public String getIdentifierPrefix() {
		return identifierPrefix;
	}
	
}
