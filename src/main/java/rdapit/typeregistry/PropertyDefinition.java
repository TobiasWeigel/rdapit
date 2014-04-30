package rdapit.typeregistry;

import rdapit.PID;

/**
 * Encapsulates a single property definition in the type registry.
 * 
 */
public class PropertyDefinition {

	protected PID identifier;
	
	protected String name;
	protected PID valueType;
	protected String namespace;
	
}
