package rdapit.pidsystem;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A simple PID record encapsulation.
 * 
 */
public class PIDRecord {

	private final String pid;

	private HashMap<String, String> properties;

	public PIDRecord(String pid) {
		this.pid = pid;
		this.properties = new HashMap<String, String>();
	}

	@JsonCreator
	private PIDRecord(@JsonProperty("handle") String identifier, @JsonProperty("values") Map<String, String> properties) {
		this.pid = new String(identifier);
		this.properties = new HashMap<String, String>(properties);
	}

	public static PIDRecord fromJson(String inputString) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(inputString);
		PIDRecord pidRecord = new PIDRecord(new String(root.get("handle").asText()));
		for (JsonNode valueNode : root.get("values")) {
			// key, ValueType, value --> all Strings!
			throw new UnsupportedOperationException("not yet implemented");
		}
		return pidRecord;
	}

	public void addProperties(Map<String, String> newProperties) {
		this.properties.putAll(newProperties);
	}

	@JsonProperty("values")
	public Collection<Map<String, String>> getProperties() {
		Collection<Map<String, String>> result = new LinkedList<Map<String, String>>();
		int idx = 0;
		for (String key : properties.keySet()) {
			idx += 1;
			Map<String, String> handleValue = new HashMap<String, String>();
			handleValue.put("index", "" + idx);
			handleValue.put("type", key);
			handleValue.put("data", properties.get(key));
			result.add(handleValue);
		}
		return result;
	}
}
