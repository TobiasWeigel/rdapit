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
	 * Resolves an identifier and returns the full PID record or null if it is
	 * not registered.
	 * 
	 * @param pid
	 * @return null if the pid is not registered or a Map<String, String> from
	 *         property identifiers (not names!) to values.
	 */
	public Map<String, String> getAllProperties(String pid);

	/**
	 * Retrieves a property definition
	 * 
	 * @param propertyIdentifier
	 * @return null if there is no property with given identifier, the definition record otherwise.
	 * @throws IOException 
	 */
	public PropertyDefinition describeProperty(String propertyIdentifier) throws IOException;

	/**
	 * Retrieves a type definition
	 * 
	 * @param typeIdentifier
	 * @return null if there is no type with given identifier, the definition record otherwise.
	 */
	public TypeDefinition describeType(String typeIdentifier);

	public boolean conformsToType(String pid, String typeIdentifier);

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

}