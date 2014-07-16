package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pidsystem.PID;
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
	protected final PropertyDefinition pitmarkerPropertyDefinition;

	public TypingService(IIdentifierSystem identifierSystem, ITypeRegistry typeRegistry) throws IOException {
		super();
		this.identifierSystem = identifierSystem;
		this.typeRegistry = typeRegistry;
		this.pitmarkerPropertyDefinition = typeRegistry.queryPropertyDefinition(new PID(PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY));
	}

	@Override
	public boolean isIdentifierRegistered(PID pid) throws IOException {
		return identifierSystem.isIdentifierRegistered(pid);
	}

	@Override
	public String queryProperty(PID pid, PropertyDefinition propertyDefinition) throws IOException {
		return identifierSystem.queryProperty(pid, propertyDefinition);
	}

	@Override
	public String queryProperty(PID pid, String propertyName, ITypeRegistry typeRegistry) throws IOException {
		return identifierSystem.queryProperty(pid, propertyName, typeRegistry);
	}

	@Override
	public PID registerPID(Map<String, String> properties) throws IOException {
		return identifierSystem.registerPID(properties);
	}

	@Override
	public Map<String, String> queryByType(PID pid, TypeDefinition typeDefinition) throws IOException {
		return identifierSystem.queryByType(pid, typeDefinition);
	}

	@Override
	public Map<String, String> queryByType(PID pid, PID typeIdentifier, ITypeRegistry typeRegistry) throws IOException {
		return identifierSystem.queryByType(pid, typeIdentifier, typeRegistry);
	}

	@Override
	public void deletePID(PID pid) {
		identifierSystem.deletePID(pid);
	}

	@Override
	public Map<String, String> getAllProperties(PID pid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public PropertyDefinition describeProperty(PID propertyIdentifier) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public TypeDefinition describeType(PID typeIdentifier) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean conformsToType(PID pid, PID typeIdentifier) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Object genericResolve(PID pid) throws IOException {
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
			return getAllProperties(pid);
		}
	}

	@Override
	public boolean isTypeRegistryPID(PID pid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet");
	}

}
