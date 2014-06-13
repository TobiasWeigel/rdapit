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
	protected final PID valueType;
	protected final String namespace;
	
	public PropertyDefinition(PID identifier, String name, PID valueType) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.valueType = valueType;
		this.namespace = "";
	}
	public PID getIdentifier() {
		return identifier;
	}
	public String getName() {
		return name;
	}
	public PID getValueType() {
		return valueType;
	}
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Instance-based factory method. Fabricates a concrete instance of a property with a valid ValueType and the given value.  
	 * 
	 * @param value an instance of the proper value type
	 * @return
	 *   an instance of Property parameterized with the ValueType
	 */
	public Property<?> generateProperty(Object value) {
		if (valueType.getIdentifierName().equals(ELEMENTAL_VALUETYPE_STRING)) {
			if (!(value instanceof String)) throw new IllegalArgumentException("Expected instance of String, got instance of "+value.getClass());
			return new Property<String>(name, valueType.getIdentifierName(), (String) value); // TODO: valueType identifierName is not entirely correct, must be read from an entry in that PID's record!
		}
		else throw new IllegalStateException("Unknown elemental value type: "+valueType.getIdentifierName());
	}
	
	
	
}
