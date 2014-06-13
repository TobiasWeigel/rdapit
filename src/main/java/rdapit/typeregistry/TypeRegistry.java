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

import rdapit.PID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TypeRegistry implements ITypeRegistry {

	protected final URI baseURI;
	protected Client client;
	protected WebTarget rootTarget;
	protected WebTarget searchTarget;

	public TypeRegistry(String baseURI) {
		this.baseURI = UriBuilder.fromUri(baseURI).build();
		this.client = ClientBuilder.newBuilder().build();
		this.rootTarget = client.target(baseURI);
		this.searchTarget = rootTarget.path("search").path("DataType");
	}

	@Override
	public PropertyDefinition queryPropertyDefinition(PID propertyIdentifier) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException {
		String response = searchTarget.queryParam("query", propertyName).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(response);
		List<PropertyDefinition> results = new ArrayList<PropertyDefinition>();
		if (rootNode.get("code").asInt() != 200) throw new IOException("Error querying the type registry: "+rootNode.get("code"));
		for (JsonNode entry: rootNode.get("extras").get("data")) {
			if (!entry.get("model").asText().equals("DataType")) continue;
			// crawl through the key_value list of this type record to find the 'name' and 'type' of the property
			String valuetype = "";
			String propertyNameFromRecord = "";
			for (JsonNode entryKV: entry.get("key_value")) {
				if (entryKV.get("key").asText().equalsIgnoreCase("name") && entryKV.get("val").asText().equalsIgnoreCase(propertyName))
					propertyNameFromRecord = entryKV.get("val").asText();
				else if (entryKV.get("key").asText().equalsIgnoreCase("type")) valuetype = entryKV.get("val").asText();
			}
			if (!valuetype.isEmpty() && !propertyNameFromRecord.isEmpty()) {
				// found both value type and original property name; add valid property definition to result list
				results.add(new PropertyDefinition(new PID(entry.get("ID").asText()), propertyNameFromRecord, new PID(valuetype)));
			}
		}
		return results;
	}

	@Override
	public void createPropertyDefinition(PID propertyIdentifier, PropertyDefinition propertyDefinition) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public TypeDefinition queryTypeDefinition(PID typeIdentifier) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition) {
		throw new UnsupportedOperationException("not implemented yet");
	}

}
