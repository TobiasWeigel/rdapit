package rdapit.pidsystem;

import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rdapit.pitservice.PIDInformation;
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
	public String registerPID(Map<String, String> properties) throws IOException {
		String p = UUID.randomUUID().toString();
		storage.put(p, new HashMap<>(properties));
		return p;
	}

	@Override
	public PIDInformation queryByType(String pid, TypeDefinition typeDefinition) throws IOException {
		Map<String, String> map = storage.get(pid);
		PIDInformation pidinfo = new PIDInformation();
		for (String propID : typeDefinition.getAllProperties()) {
			String s = map.get(propID);
			if (s != null)
				pidinfo.addProperty(propID, "", s);
		}
		return pidinfo;
	}

	@Override
	public boolean deletePID(String pid) {
		return storage.remove(pid) != null;
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public PIDInformation queryAllProperties(String pid) throws IOException {
		Map<String, String> map = storage.get(pid);
		PIDInformation result = new PIDInformation();
		for (String p : map.keySet()) {
			result.addProperty(p, "", map.get(p));
		}
		return result;
	}

}
