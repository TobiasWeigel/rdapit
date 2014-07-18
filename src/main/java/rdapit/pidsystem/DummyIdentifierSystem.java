package rdapit.pidsystem;

import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * Dummy implementation of the IIdentifierSystem interface
 * 
 */
public class DummyIdentifierSystem implements IIdentifierSystem {

	private HashMap<String, HashMap<String, String>> storage = new HashMap<>();

	@Override
	public boolean isIdentifierRegistered(String pid) throws IOException {
		return storage.containsKey(pid);
	}

	@Override
	public String queryProperty(String pid, PropertyDefinition propertyDefinition) throws IOException {
		return storage.get(pid).get(propertyDefinition.getIdentifier());
	}

	@Override
	public String queryProperty(String pid, String propertyName, ITypeRegistry typeRegistry) throws IOException {
		List<PropertyDefinition> l = typeRegistry.queryPropertyDefinitionByName(propertyName);
		if (l.size() > 1)
			throw new IllegalArgumentException("Property name not unique - arbitration not supported");
		if (l.isEmpty())
			throw new IllegalArgumentException("Property with given name '" + propertyName + "' not found.");
		return queryProperty(pid, l.get(0));
	}

	@Override
	public String registerPID(Map<String, String> properties) throws IOException {
		String p = UUID.randomUUID().toString();
		storage.put(p, new HashMap<>(properties));
		return p;
	}

	@Override
	public Map<String, String> queryByType(String pid, TypeDefinition typeDefinition) throws IOException {
		Map<String, String> map = storage.get(pid);
		Map<String, String> result = new HashMap<>();
		for (PropertyDefinition pd : typeDefinition.getAllProperties()) {
			String s = map.get(pd.getIdentifier());
			if (s != null)
				result.put(pd.getIdentifier(), s);
		}
		return result;
	}

	@Override
	public Map<String, String> queryByType(String pid, String typeIdentifier, ITypeRegistry typeRegistry) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePID(String pid) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, String> queryAllProperties(String pid) throws IOException {
		Map<String, String> map = storage.get(pid);
		return map;
	}

}
