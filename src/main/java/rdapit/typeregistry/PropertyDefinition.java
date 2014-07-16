package rdapit.typeregistry;

/**
 * Encapsulates a single property definition in the type registry.
 * 
 */
public class PropertyDefinition {

	public static final String ELEMENTAL_VALUETYPE_STRING = "String";
	public static final String IDENTIFIER_PIT_MARKER_PROPERTY = "11043.4/pit.construct";

	/** a PID */
	protected final String identifier;

	protected final String name;

	/** a PID */
	protected final String range;
	protected final String namespace;

	protected final String description;

	/**
	 * Constructor.
	 * 
	 * @param identifier
	 *            a PID
	 * @param name
	 *            some readable name (not a PID)
	 * @param range
	 *            a PID, i.e. the PID of the value type space of the property.
	 *            This can also be an 'elemental type' identifier.
	 */
	public PropertyDefinition(String identifier, String name, String range) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = "";
		this.description = "";
	}

	public PropertyDefinition(String identifier, String name, String range, String namespace) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = namespace;
		this.description = "";
	}

	public PropertyDefinition(String identifier, String name, String range, String namespace, String description) {
		super();
		this.identifier = identifier;
		this.name = name;
		this.range = range;
		this.namespace = namespace;
		this.description = description;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getName() {
		return name;
	}

	public String getRange() {
		return range;
	}

	public String getNamespace() {
		return namespace;
	}

	/**
	 * Verifies that the given value conforms to the property's type range.
	 * 
	 * @param value
	 * @return true or false
	 */
	public boolean generateProperty(Object value) {
		if (range.equalsIgnoreCase(ELEMENTAL_VALUETYPE_STRING)) {
			return (value instanceof String);
		} else
			throw new IllegalStateException("Unknown elemental value type: " + range);
	}

}
