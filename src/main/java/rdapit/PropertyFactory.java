package rdapit;

public class PropertyFactory {

	/**
	 * Factory method. Generates a property which will be typed depending on the
	 * valueType parameter. Performs a consistency check on the value against
	 * the type.
	 * 
	 * @param key
	 * @param valueType
	 *            the PID name of the value type
	 * @param value
	 *            an instance of any class, must match the type
	 * @return a property instance
	 * @throws Exception
	 */
	public Property<?> generateProperty(String key, String valueType,
			Object value) throws Exception {
		// 1. locally known types
		if (valueType.equals(Property.TYPE_STRING)) {
			return new Property<String>(key, valueType, value.toString());
		}
		if (valueType.equals(Property.TYPE_INTEGER)) {
			Integer v = Integer.parseInt(value.toString());
			return new Property<Integer>(key, valueType, v);
		}
		// 2. remote types via type registry
		// 3. type unknown - fail. TODO replace with proper exception
		throw new Exception("Unknown value type!");
	}

	/**
	 * Factory method. Tries to generate a property instance by using the Java
	 * class of the given value to determine the value type.
	 * 
	 * @param key
	 * @param value
	 * @return a property instance
	 * @throws Exception
	 */
	public Property<?> generateProperty(String key, Object value)
			throws Exception {
		if (value instanceof String) {
			return generateProperty(key, Property.TYPE_STRING, value);
		}
		if (value instanceof Integer) {
			return generateProperty(key, Property.TYPE_INTEGER, value);
		}
		// TODO replace with proper exception
		throw new Exception("Unknown value type / Java type guessing failed!");
	}

}
