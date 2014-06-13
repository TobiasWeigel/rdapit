package rdapit.pidsystem;

import java.io.IOException;

import rdapit.PID;
import rdapit.Property;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;

/**
 * Main abstraction interface towards the identifier system containing
 * registered identifiers and associated state information.
 * 
 */
public interface IIdentifierSystem {

	public boolean isIdentifierRegistered(PID pid) throws IOException;

	public Property<?> queryProperty(PID pid, PropertyDefinition propertyDefinition) throws IOException;

	public Property<?> queryProperty(PID pid, String propertyName, ITypeRegistry typeRegistry) throws IOException;

}
