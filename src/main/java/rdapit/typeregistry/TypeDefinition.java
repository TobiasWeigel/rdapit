package rdapit.typeregistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates a type in the type registry, roughly defined as a set of
 * properties.
 * 
 */
public class TypeDefinition {

	/**
	 * PID of the type.
	 */
	@JsonProperty("identifier")
	protected String identifier;

	/**
	 * Value (boolean) True means mandatory, False means optional.
	 */
	protected HashMap<String, PropertyDefinitionParameters> properties;

	@JsonProperty("explanationOfUse")
	protected String explanationOfUse;
	
	@JsonProperty("description")
	protected String description;

	public TypeDefinition(String identifier, String explanationOfUse, String description) {
		this.identifier = identifier;
		this.explanationOfUse = explanationOfUse;
		this.description = description;
		this.properties = new HashMap<String, PropertyDefinitionParameters>();
	}

	@JsonCreator
	public TypeDefinition(@JsonProperty("identifier") String identifier, @JsonProperty("explanationOfUse") String explanationOfUse, @JsonProperty("description") String description,
			@JsonProperty("mandatoryProperties") Collection<String> mandatoryProperties, @JsonProperty("optionalProperties") Collection<String> optionalProperties) {
		this.identifier = identifier;
		this.explanationOfUse = explanationOfUse;
		this.description = description;
		this.properties = new HashMap<String, PropertyDefinitionParameters>();
		for (String p : mandatoryProperties) addProperty(p, true);
		for (String p : optionalProperties) addProperty(p, false);
	}
	
	
	public void addProperty(String propertyIdentifier, boolean mandatory) {
		properties.put(propertyIdentifier, new PropertyDefinitionParameters(mandatory));
	}

	/**
	 * Returns a set of all properties. The caller will not be able to
	 * distinguish between mandatory and optional properties.
	 * 
	 * @return a set of property identifiers (strings)
	 */
	@JsonIgnore
	public Set<String> getAllProperties() {
		return new HashSet<String>(properties.keySet());
	}

	@JsonIgnore
	private Set<String> getProperties(boolean mandatory) {
		Set<String> result = new HashSet<>();
		for (String pd : properties.keySet()) {
			if (properties.get(pd).isMandatory() == mandatory)
				result.add(pd);
		}
		return result;
	}

	/**
	 * Returns a set of all mandatory properties.
	 * 
	 * @return a set of properties
	 */
	@JsonProperty("mandatoryProperties")
	public Set<String> getMandatoryProperties() {
		return getProperties(true);
	}

	/**
	 * Returns a set of all optional properties.
	 * 
	 * @return a set of properties
	 */
	@JsonProperty("optionalProperties")
	public Set<String> getOptionalProperties() {
		return getProperties(false);
	}
	
	public String getIdentifier() {
		return identifier;
	}

}
