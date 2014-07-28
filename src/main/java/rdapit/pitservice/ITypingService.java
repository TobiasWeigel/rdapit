package rdapit.pitservice;

import java.io.IOException;
import java.util.Map;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * Core interface for clients to contact. Implementations will provide the
 * necessary fixation in a concrete protocol (e.g. HTTP-REST).
 * 
 */
public interface ITypingService extends IIdentifierSystem {

	/**
	 * Retrieves a property definition
	 * 
	 * @param propertyIdentifier
	 * @return null if there is no property with given identifier, the
	 *         definition record otherwise.
	 * @throws IOException
	 */
	public PropertyDefinition describeProperty(String propertyIdentifier) throws IOException;

	/**
	 * Retrieves a type definition
	 * 
	 * @param typeIdentifier
	 * @return null if there is no type with given identifier, the definition
	 *         record otherwise.
	 * @throws IOException
	 */
	public TypeDefinition describeType(String typeIdentifier) throws IOException;

	public boolean conformsToType(String pid, String typeIdentifier) throws IOException;

	/**
	 * Resolves the given PID without previous knowledge about the kind of
	 * entity it identifies (e.g. a common PID record, a property or type
	 * definition etc.).
	 * 
	 * @param pid
	 * @return The returned object can be either a PID record, a property
	 *         definition or a type definition. It can also be null, indicating
	 *         the PID is not registered at all.
	 * @throws IOException
	 */
	public Object genericResolve(String pid) throws IOException;

	/**
	 * Queries a single property from the PID.
	 * 
	 * @param pid
	 * @param propertyNameOrID
	 *            the method will decide whether the given String is a unique
	 *            identifier or a (potentially ambiguous) name.
	 * @return a PIDInformation object containing the single property name and
	 *         value or null if the property is undefined.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             if the property is defined but ambiguous (type registry query
	 *             returned multiple results).
	 */
	public PIDInformation queryProperty(String pid, String propertyNameOrID) throws IOException;

	/**
	 * Queries all properties of a type available from the given PID. If
	 * optional properties are present, they will be returned as well. If there
	 * are mandatory properties missing (i.e. the record of the given PID does
	 * not fully conform to the type), the method will NOT fail but simply
	 * return only those properties that are present.
	 * 
	 * @param pid
	 * @param typeIdentifier
	 *            a type identifier, not a name
	 * @return a map with property identifiers mapping to values. Contains all
	 *         property values present in the record of the given PID that are
	 *         also specified by the type (mandatory or optional). If the pid is
	 *         not registered, the method returns null.
	 * @throws IOException
	 */
	public Map<String, String> queryByType(String pid, String typeIdentifier) throws IOException;

}