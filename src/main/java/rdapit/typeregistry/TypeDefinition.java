package rdapit.typeregistry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	protected HashMap<PropertyDefinition, PropertyDefinitionParameters> properties;

	protected String name;
	protected String description;

	public TypeDefinition(String identifier, String name, String description) {
		this.identifier = identifier;
		this.name = name;
		this.description = description;
		this.properties = new HashMap<PropertyDefinition, PropertyDefinitionParameters>();
	}

	public void addProperty(PropertyDefinition propertyDefinition, boolean mandatory) {
		properties.put(propertyDefinition, new PropertyDefinitionParameters(mandatory));
	}

	/**
	 * Returns a set of all property definitions. The caller will not be able to
	 * distinguish between mandatory and optional properties.
	 * 
	 * @return a set of property definitions
	 */
	public Set<PropertyDefinition> getAllProperties() {
		return new HashSet<PropertyDefinition>(properties.keySet());
	}

	private Set<PropertyDefinition> getProperties(boolean mandatory) {
		Set<PropertyDefinition> result = new HashSet<>();
		for (PropertyDefinition pd : properties.keySet()) {
			if (properties.get(pd).isMandatory() == mandatory)
				result.add(pd);
		}
		return result;
	}

	/**
	 * Returns a set of all mandatory property definitions.
	 * 
	 * @return a set of property definitions
	 */
	public Set<PropertyDefinition> getMandatoryProperties() {
		return getProperties(true);
	}

	/**
	 * Returns a set of all optional property definitions.
	 * 
	 * @return a set of property definitions
	 */
	public Set<PropertyDefinition> getOptionalProperties() {
		return getProperties(false);
	}

}
