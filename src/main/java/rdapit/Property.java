package rdapit;

public class Property<VT> {
	
	// These constants are ugly, but the value types must be String since they are PID names.
	// Not every type is known locally.
	// Could replace this with an enum or a registry.
	public static final String TYPE_STRING = "0.TYPE/STRING";
	public static final String TYPE_INTEGER = "0.TYPE/INTEGER";

	private String key; // is a PID
	private String valueType;
	private VT value;
	
	public Property(String key, String valueType, VT value) {
		this.key = key;
		this.value = value;
		this.valueType = valueType;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValueType() {
		return valueType;
	}
	
	public VT getValue() {
		return value;
	}

}
