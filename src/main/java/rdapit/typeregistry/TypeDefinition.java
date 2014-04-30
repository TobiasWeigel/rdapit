package rdapit.typeregistry;

import java.util.HashSet;

import rdapit.PID;

/**
 * Encapsulates a type in the type registry, roughly defined as a set of
 * properties.
 * 
 */
public class TypeDefinition {

	protected PID identifier;

	protected HashSet<PropertyDefinition> mandatoryProperties;
	protected HashSet<PropertyDefinition> optionalProperties;

	protected String name;
	protected String description;

}
