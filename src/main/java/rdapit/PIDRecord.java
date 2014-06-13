package rdapit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PIDRecord {

	private final PID pid;
	
	private HashMap<String, Property<?>> properties;
	
	public PIDRecord(PID pid) {
		this.pid = pid;
		this.properties = new HashMap<String, Property<?>>();
	}
	
	
	@JsonCreator
	private PIDRecord(@JsonProperty("handle") String identifier, @JsonProperty("values") Map<String, Property<?>> properties) {
		this.pid = new PID(identifier);
		this.properties = new HashMap<String, Property<?>>(properties);
	}
	
	public static PIDRecord fromJson(String inputString) throws JsonProcessingException, IOException  {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(inputString);
		PIDRecord pidRecord = new PIDRecord(new PID(root.get("handle").asText()));
		for (JsonNode valueNode: root.get("values")) {
			// key, ValueType, value --> all Strings!
			throw new UnsupportedOperationException("not yet implemented");
		}
		return pidRecord;
	} 
	
}
