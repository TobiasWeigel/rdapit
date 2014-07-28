package rdapit.pitservice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * Core implementation class that offers the combined higher-level services
 * through a type registry and an identifier system.
 * 
 */
public class TypingService implements ITypingService {

	protected final IIdentifierSystem identifierSystem;
	protected final ITypeRegistry typeRegistry;

	public TypingService(IIdentifierSystem identifierSystem, ITypeRegistry typeRegistry) throws IOException {
		super();
		this.identifierSystem = identifierSystem;
		this.typeRegistry = typeRegistry;
	}

	@Override
	public boolean isIdentifierRegistered(String pid) throws IOException {
		return identifierSystem.isIdentifierRegistered(pid);
	}

	@Override
	public String queryProperty(String pid, PropertyDefinition propertyDefinition) throws IOException {
		return identifierSystem.queryProperty(pid, propertyDefinition);
	}

	@Override
	public String registerPID(Map<String, String> properties) throws IOException {
		return identifierSystem.registerPID(properties);
	}

	@Override
	public Map<String, String> queryByType(String pid, TypeDefinition typeDefinition) throws IOException {
		return identifierSystem.queryByType(pid, typeDefinition);
	}

	@Override
	public boolean deletePID(String pid) {
		return identifierSystem.deletePID(pid);
	}

	@Override
	public PropertyDefinition describeProperty(String propertyIdentifier) throws IOException {
		return typeRegistry.queryPropertyDefinition(propertyIdentifier);
	}

	@Override
	public TypeDefinition describeType(String typeIdentifier) throws IOException {
		return typeRegistry.queryTypeDefinition(typeIdentifier);
	}

	@Override
	public boolean conformsToType(String pid, String typeIdentifier) throws IOException {
		// resolve type record
		TypeDefinition typeDef = typeRegistry.queryTypeDefinition(typeIdentifier);
		if (typeDef == null)
			throw new IllegalArgumentException("Unknown type: " + typeIdentifier);
		// resolve PID
		Map<String, String> props = identifierSystem.queryAllProperties(pid);
		/*
		 * Now go through all mandatory properties of the type and check whether
		 * they are in the pid data. Remember: both the keys of the pid data map
		 * and the type definition record properties are property identifiers
		 * (not names)!
		 */
		for (String p : typeDef.getMandatoryProperties()) {
			if (!props.containsKey(p))
				return false;
		}
		return true;
	}

	@Override
	public Object genericResolve(String pid) throws IOException {
		// ask identifier system whether this is a type registry record
		boolean istypereg = identifierSystem.isTypeRegistryPID(pid);
		if (istypereg) {
			Object obj = typeRegistry.query(pid);
			if (obj == null) {
				throw new IOException(
						"Conflicting records: Identifier registered in PID system and indicating a registry entry, but not in type registry / registered in an unknown type registry!");
			}
			if (obj instanceof PropertyDefinition) {
				return (PropertyDefinition) obj;
			} else if (obj instanceof TypeDefinition) {
				return (TypeDefinition) obj;
			} else
				throw new IOException("Unknown kind of type registry entry!");
		} else {
			// this is a generic PID record (or unresolvable)
			return queryAllProperties(pid);
		}
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet");
	}

	@Override
	public Map<String, String> queryAllProperties(String pid) throws IOException {
		return identifierSystem.queryAllProperties(pid);
	}

	@Override
	public PIDInformation queryProperty(String pid, String propertyIdentifier) throws IOException {
		PIDInformation pidInfo = new PIDInformation();
		// query type registry
		PropertyDefinition propDef = typeRegistry.queryPropertyDefinition(propertyIdentifier);
		if (propDef != null) {
			pidInfo.addProperty(propDef.getName(), identifierSystem.queryProperty(pid, propDef)); 
			return pidInfo;
		}
		return null;
	}

	@Override
	public Map<String, String> queryByType(String pid, String typeIdentifier) throws IOException {
		TypeDefinition typeDef = typeRegistry.queryTypeDefinition(typeIdentifier);
		if (typeDef == null)
			return null;
		// now query PID record
		Map<String, String> result = identifierSystem.queryByType(pid, typeDef);
		return result;
	}
	
	public PIDInformation queryByTypeWithConformance(String pid, String typeIdentifier) throws IOException {
		TypeDefinition typeDef = typeRegistry.queryTypeDefinition(typeIdentifier);
		if (typeDef == null)
			return null;
		// now query PID record
		Map<String, String> values = identifierSystem.queryByType(pid, typeDef);
		PIDInformation pidInfo = new PIDInformation(values);
		pidInfo.checkTypeConformance(typeDef);
		return pidInfo;
	}
	
		
	

}
