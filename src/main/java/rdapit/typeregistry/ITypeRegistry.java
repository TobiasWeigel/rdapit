package rdapit.typeregistry;

import java.io.IOException;
import java.util.List;

import rdapit.pidsystem.PID;

/**
 * Main abstraction interface towards the type registry. Contains all methods
 * required from the registry by the core services.
 * 
 * @author Tobias Weigel
 * 
 */
public interface ITypeRegistry {

	/**
	 * Retrieves a property definition by its unique identifier.
	 * 
	 * @param propertyIdentifier
	 * @return the property definition or null if there is no such definition.
	 * @throws IOException
	 *             on communication errors with a remote registry
	 */
	public PropertyDefinition queryPropertyDefinition(PID propertyIdentifier) throws IOException;

	/**
	 * Retrieves a property definition by its property name. Note that the name
	 * is not unique, thus the method returns a List.
	 * 
	 * @param propertyName
	 * @return a list with any number of entries (may be empty).
	 * @throws IOException
	 *             on communication errors with a remote registry
	 */
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException;

	/**
	 * Registers a new property definition in the type registry.
	 * 
	 * @param propertyDefinition
	 *            The property definition to register. This also includes the
	 *            PID of the new property definition. If there is already a
	 *            property definition with this PID, it will be overwritten.
	 * @throws IOException
	 *             on communication errors with a remote registry
	 */
	public void createPropertyDefinition(PropertyDefinition propertyDefinition) throws IOException;

	/**
	 * Removes the property definition with given PID. If there is no definition
	 * with given PID, the method will do nothing.
	 * 
	 * @param propertyIdentifier
	 * @throws IOException
	 *             on communication errors with a remote registry
	 */
	public void removePropertyDefinition(PID propertyIdentifier) throws IOException;

	public TypeDefinition queryTypeDefinition(PID typeIdentifier);

	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition);


}
