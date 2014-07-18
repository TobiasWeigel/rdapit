package rdapit.typeregistry;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeRegistry implements ITypeRegistry {

	protected final URI baseURI;
	protected Client client;
	protected WebTarget rootTarget;
	protected WebTarget searchTarget;
	protected WebTarget idTarget;

	public TypeRegistry(String baseURI) {
		this.baseURI = UriBuilder.fromUri(baseURI).build();
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
		if (entry.has("key_value")) {
			for (JsonNode entryKV : entry.get("key_value")) {
				if (entryKV.get("key").asText().equalsIgnoreCase("name"))
					propName = entryKV.get("val").asText();
				else if (entryKV.get("key").asText().equalsIgnoreCase("range"))
					valuetype = entryKV.get("val").asText();
			}
		}
		PropertyDefinition result = new PropertyDefinition(entry.get("ID").asText(), propName, valuetype);
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
			if (entry.has("key_value")) {
				for (JsonNode entryKV : entry.get("key_value")) {
					if (entryKV.get("key").asText().equalsIgnoreCase("name") && entryKV.get("val").asText().equalsIgnoreCase(propertyName))
						propertyNameFromRecord = entryKV.get("val").asText();
					else if (entryKV.get("key").asText().equalsIgnoreCase("range"))
						valuetype = entryKV.get("val").asText();
				}
			}
			if (!valuetype.isEmpty() && !propertyNameFromRecord.isEmpty()) {
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
	public TypeDefinition queryTypeDefinition(String typeIdentifier) {
		throw new UnsupportedOperationException("not implemented yet");
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

}
