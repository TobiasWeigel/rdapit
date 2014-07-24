package rdapit.typeregistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Encapsulates a type in the type registry, roughly defined as a set of
 * properties.
 * 
 */
public class TypeDefinition {

	/**
	 * PID of the type.
	 */
	protected String identifier;

	/**
	 * Value (boolean) True means mandatory, False means optional.
	 */
	protected HashMap<String, PropertyDefinitionParameters> properties;

	protected String name;
	protected String description;

	public TypeDefinition(String identifier, String name, String description) {
		this.identifier = identifier;
		this.name = name;
		this.description = description;
		this.properties = new HashMap<String, PropertyDefinitionParameters>();
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
	public Set<String> getMandatoryProperties() {
		return getProperties(true);
	}

	/**
	 * Returns a set of all optional properties.
	 * 
	 * @return a set of properties
	 */
	public Set<String> getOptionalProperties() {
		return getProperties(false);
	}

}
