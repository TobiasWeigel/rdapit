package rdapit.pitservice;

import java.util.HashMap;
import java.util.Map;

import rdapit.typeregistry.TypeDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class for storing and managing a PID record that carries meta-information
 * such as conformance flags.
 * 
 */
@JsonInclude(Include.NON_EMPTY)
public class PIDInformation {

	@JsonProperty("values")
	private HashMap<String, String> propertyValues;

	@JsonProperty("conformance")
	private HashMap<String, Boolean> conformance = new HashMap<>();

	public PIDInformation() {
		super();
		this.propertyValues = new HashMap<>();
	}
	
	public PIDInformation(Map<String, String> propertyValues) {
		super();
		this.propertyValues = new HashMap<>(propertyValues);
	}

	/**
	 * Checks whether the stored property values conform to the given type and
	 * stores the result of the conformance checks in the local information
	 * record.
	 * 
	 * @param typeDef
	 * @return true if all mandatory properties of the type are present
	 */
	public boolean checkTypeConformance(TypeDefinition typeDef) {
		boolean conf = true;
		for (String p : typeDef.getMandatoryProperties()) {
			if (!propertyValues.containsKey(p)) {
				conf = false;
				break;
			}
		}
		conformance.put(typeDef.getIdentifier(), conf);
		return conf;
	}
	
	@JsonIgnore
	public Map<String, String> getPropertyValues() {
		return new HashMap<String, String>(propertyValues);
	}
	
	public void addProperty(String propertyName, String propertyValue) {
		propertyValues.put(propertyName, propertyValue);
	}

}
