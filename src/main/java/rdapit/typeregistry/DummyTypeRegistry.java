package rdapit.typeregistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rdapit.pitservice.EntityClass;

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
		entry.put("valuetype", propertyDefinition.getRange());
		entry.put("namespace", propertyDefinition.getNamespace());
		typeStorage.put(propertyDefinition.getIdentifier(), entry);
	}

	@Override
	public PropertyDefinition queryPropertyDefinition(String propertyIdentifier) throws IOException {
		Map<String, String> entry = typeStorage.get(propertyIdentifier);
		if (entry == null) return null;
		return new PropertyDefinition(propertyIdentifier, entry.get("name"), entry.get("valuetype"), entry.get("namespace"));
	}

	@Override
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException {
		List<PropertyDefinition> results = new ArrayList<PropertyDefinition>();
		for (String key: typeStorage.keySet()) {
			Map<String, String> entry = typeStorage.get(key);
			if (entry.get("name").equals(propertyName)) {
				results.add(new PropertyDefinition(key, entry.get("name"), entry.get("valuetype"), entry.get("namespace")));
			}
		}
		return results;
	}

	@Override
	public TypeDefinition queryTypeDefinition(String typeIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createTypeDefinition(String typeIdentifier, TypeDefinition typeDefinition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyDefinition(String propertyIdentifier) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object query(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityClass determineEntityClass(String identifier) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		// TODO Auto-generated method stub
		return false;
	}

}
