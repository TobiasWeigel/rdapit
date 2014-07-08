package rdapit.typeregistry;

import java.util.HashMap;

import rdapit.PID;

/**
 * Encapsulates a type in the type registry, roughly defined as a set of
 * properties.
 * 
 */
public class TypeDefinition {
	
	protected PID identifier;

	/**
	 * Value (boolean) True means mandatory, False means optional. 
	 */
	protected HashMap<PropertyDefinition, PropertyDefinitionParameters> properties;

	protected String name;
	protected String description;
	
	public TypeDefinition(PID identifier, String name, String description) {
		this.identifier = identifier;
		this.name = name;
		this.description = description;
		this.properties = new HashMap<PropertyDefinition, PropertyDefinitionParameters>();
	}
	
	public void addProperty(PropertyDefinition propertyDefinition, boolean mandatory) {
		properties.put(propertyDefinition, new PropertyDefinitionParameters(mandatory));
	}

}
