package rdapit.pidsystem;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import rdapit.PID;
import rdapit.Property;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * Main abstraction interface towards the identifier system containing
 * registered identifiers and associated state information.
 * 
 */
public interface IIdentifierSystem {

	public boolean isIdentifierRegistered(PID pid) throws IOException;

	/**
	 * Queries a single property from the given PID.
	 * 
	 * @param pid
	 * @param propertyDefinition
	 * @return the property including a value or null if there is no property of
	 *         given name defined in this PID record.
	 * @throws IOException
	 */
	public Property<?> queryProperty(PID pid, PropertyDefinition propertyDefinition) throws IOException;

	public Property<?> queryProperty(PID pid, String propertyName, ITypeRegistry typeRegistry) throws IOException;

	/**
	 * Registers a new PID with given property values. The method decides on a
	 * PID name automatically, guaranteeing its uniqueness and preventing
	 * failure due to potential overwrites.
	 * 
	 * @param properties
	 * @return the name of the registered PID
	 */
	public PID registerPID(Collection<Property<?>> properties);

	/**
	 * Queries all properties of a given type available from the given PID. If
	 * optional properties are present, they will be returned as well. If there
	 * are mandatory properties missing (i.e. the record of the given PID does
	 * not fully conform to the type), the method will NOT fail but simply
	 * return only those properties that are present.
	 * 
	 * @param pid
	 * @param typeDefinition
	 * @return all property values present in the record of the given PID.
	 * @throws IOException
	 */
	public Set<Property<?>> queryByType(PID pid, TypeDefinition typeDefinition) throws IOException;

	public Set<Property<?>> queryByType(PID pid, PID typeIdentifier, ITypeRegistry typeRegistry) throws IOException;

}
