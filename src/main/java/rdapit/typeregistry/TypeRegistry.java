package rdapit.typeregistry;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import rdapit.pitservice.EntityClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeRegistry implements ITypeRegistry {

	protected final URI baseURI;
	protected String identifierPrefix;

	protected Client client;
	protected WebTarget rootTarget;
	protected WebTarget searchTarget;
	protected WebTarget idTarget;

	public TypeRegistry(String baseURI, String identifierPrefix) {
		this.baseURI = UriBuilder.fromUri(baseURI).build();
		this.identifierPrefix = identifierPrefix;
		client = ClientBuilder.newBuilder().build();
		rootTarget = client.target(baseURI);
		searchTarget = rootTarget.path("search").path("DataType");
		idTarget = rootTarget.path("records").path("{id}");
	}

	@Override
	public PropertyDefinition queryPropertyDefinition(String propertyIdentifier) throws IOException {
		String response = idTarget.resolveTemplate("id", propertyIdentifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		JsonNode entry = rootNode.get("extras").get("data");
		String propName = "";
		String valuetype = "";
		String namespace = "";
		String description = "";
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				String key = entryKV.get("key").asText();
				if (key.equalsIgnoreCase("name"))
					propName = entryKV.get("val").asText();
				else if (key.equals(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY) && !entryKV.get("val").asText().equalsIgnoreCase("PROPERTY_DEFINITION"))
					// this is not a property record!
					return null;
				else if (key.equalsIgnoreCase("range"))
					valuetype = entryKV.get("val").asText();
				else if (key.equalsIgnoreCase("namespace"))
					namespace = entryKV.get("val").asText();
				else if (key.equalsIgnoreCase("description"))
					description = entryKV.get("val").asText();
				
			}
		}
		PropertyDefinition result = new PropertyDefinition(entry.get("ID").asText(), propName, valuetype, namespace, description);
		return result;
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
			if (entry.has("key_value")) {
				for (JsonNode entryKV : entry.get("key_value")) {
					if (entryKV.get("key").asText().equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY)) {
						if (entryKV.get("val").asText().equalsIgnoreCase("PROPERTY_DEFINITION"))
							isPropDef = true;
					}
					if (entryKV.get("key").asText().equalsIgnoreCase("name") && entryKV.get("val").asText().equalsIgnoreCase(propertyName))
						propertyNameFromRecord = entryKV.get("val").asText();
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

	@Override
	public void createPropertyDefinition(PropertyDefinition propertyDefinition) {
		throw new UnsupportedOperationException("not implemented yet");
		// TODO: also store PIT.Construct field, PIT.Version
	}

	@Override
	public TypeDefinition queryTypeDefinition(String typeIdentifier) throws IOException {
		String response = idTarget.resolveTemplate("id", typeIdentifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		JsonNode entry = rootNode.get("extras").get("data");
		String typeName = "";
		String description = "";
		Map<String, Boolean> properties = new HashMap<>();
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				String key = entryKV.get("key").asText();
				if (key.equalsIgnoreCase("name"))
					typeName = entryKV.get("val").asText();
				else if (key.equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY) && !entryKV.get("val").asText().equalsIgnoreCase("TYPE_DEFINITION"))
					// this is not a type record!
					return null;
				else if (key.equalsIgnoreCase("description"))
					description = entryKV.get("val").asText();
				else if (key.equalsIgnoreCase("property")) {
					// This value is a small json snippet with the property ID
					// and a mandatory flag
					JsonNode propNode = mapper.readTree(entryKV.get("val").asText());
					properties.put(propNode.get("id").asText(), propNode.get("mandatory").asBoolean());
				}
			}
		}
		TypeDefinition result = new TypeDefinition(entry.get("ID").asText(), typeName, description);
		// add properties
		for (String pd : properties.keySet())
			result.addProperty(pd, properties.get(pd));
		return result;
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
	public Object query(String identifier) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public EntityClass determineEntityClass(String identifier) throws IOException {
		// retrieve full record and analyze marker field
		String response = idTarget.resolveTemplate("id", identifier).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		if (rootNode.get("code").asInt() != 200)
			return null;
		JsonNode entry = rootNode.get("extras").get("data");
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				if (entryKV.get("key").asText().equalsIgnoreCase(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY)) {
					String v = entryKV.get("val").asText();
					if (v.equalsIgnoreCase("PROPERTY_DEFINITION")) 
						return EntityClass.PROPERTY;
					if (v.equalsIgnoreCase("TYPE_DEFINITION")) 
						return EntityClass.TYPE;
					throw new IllegalStateException("Unknown value for "+PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY+" in record "+identifier+": "+v);
				}
			}
		}
		return null;
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		return pid.startsWith(identifierPrefix);
	}
	
}
