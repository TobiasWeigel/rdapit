package rdapit.typeregistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import rdapit.pidsystem.PID;

/**
 * Mock-up for the type registry. Stores information locally. Used for unit testing.
 * 
 */
public class DummyTypeRegistry implements ITypeRegistry {

	private HashMap<String, Map<String, String>> typeStorage = new HashMap<>();
	
	@Override
	public void createPropertyDefinition(PropertyDefinition propertyDefinition) {
		HashMap<String, String> entry = new HashMap<>();
		entry.put("name", propertyDefinition.getName());
		entry.put("valuetype", propertyDefinition.getValueType().getIdentifierName());
		entry.put("namespace", propertyDefinition.getNamespace());
		typeStorage.put(propertyDefinition.getIdentifier().getIdentifierName(), entry);
	}

	@Override
	public PropertyDefinition queryPropertyDefinition(PID propertyIdentifier) throws IOException {
		Map<String, String> entry = typeStorage.get(propertyIdentifier.getIdentifierName());
		if (entry == null) return null;
		return new PropertyDefinition(new PID(propertyIdentifier.getIdentifierName()), entry.get("name"), new PID(entry.get("valuetype")), entry.get("namespace"));
	}

	@Override
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException {
		List<PropertyDefinition> results = new ArrayList<PropertyDefinition>();
		for (String key: typeStorage.keySet()) {
			Map<String, String> entry = typeStorage.get(key);
			if (entry.get("name").equals(propertyName)) {
				results.add(new PropertyDefinition(new PID(key), entry.get("name"), new PID(entry.get("valuetype")), entry.get("namespace")));
			}
		}
		return results;
	}

	@Override
	public TypeDefinition queryTypeDefinition(PID typeIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyDefinition(PID propertyIdentifier) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object query(PID identifier) {
		// TODO Auto-generated method stub
		return null;
	}

}
