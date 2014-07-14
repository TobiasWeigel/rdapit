package rdapit.typeregistry;

import rdapit.PID;
import rdapit.Property;

/**
 * Encapsulates a single property definition in the type registry.
 * 
 */
public class PropertyDefinition {
	
	public static final String ELEMENTAL_VALUETYPE_STRING = "String";

	protected final PID identifier;
	
	protected final String name;
	protected final PID range;
	protected final String namespace;
	
	protected final String description;
	
	public PropertyDefinition(PID identifier, String name, PID range) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = "";
		this.description = "";
	}
	
	
	public PropertyDefinition(PID identifier, String name, PID range, String namespace) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = namespace;
		this.description = "";
	}
	
	public PropertyDefinition(PID identifier, String name, PID range, String namespace, String description) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = namespace;
		this.description = description;
	}


	public PID getIdentifier() {
		return identifier;
	}
	public String getName() {
		return name;
	}
	public PID getValueType() {
		return range;
	}
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Verifies that the given value conforms to the property's type range.  
	 * 
	 * @param value 
	 * @return
	 *   true or false
	 */
	public boolean generateProperty(Object value) {
		if (range.getIdentifierName().equalsIgnoreCase(ELEMENTAL_VALUETYPE_STRING)) {
			return (value instanceof String);
		}
		else throw new IllegalStateException("Unknown elemental value type: "+range.getIdentifierName());
	}
	
	
	
}
