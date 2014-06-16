package rdapit.typeregistry;

import java.io.IOException;
import java.util.List;

import rdapit.PID;

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
	 * @throws IOException on communication errors with a remote registry
	 */
	public PropertyDefinition queryPropertyDefinition(PID propertyIdentifier) throws IOException;
	
	/**
	 * Retrieves a property definition by its property name. Note that the name
	 * is not unique, thus the method returns a List.
	 * 
	 * @param propertyName
	 * @return a list with any number of entries (may be empty).
	 * @throws IOException on communication errors with a remote registry
	 */
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException;
	
	public void createPropertyDefinition(PID propertyIdentifier, PropertyDefinition propertyDefinition);
	
	public TypeDefinition queryTypeDefinition(PID typeIdentifier);

	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition);

}
