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
	
	public PropertyDefinition queryPropertyDefinition(PID propertyIdentifier);
	
	/**
	 * Retrieves a property definition by its property name. Note that the name
	 * is not unique, thus the method returns a List.
	 * 
	 * @param propertyName
	 * @return a list with any number of entries (may be empty).
	 * @throws IOException 
	 */
	public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException;
	
	public void createPropertyDefinition(PID propertyIdentifier, PropertyDefinition propertyDefinition);
	
	public TypeDefinition queryTypeDefinition(PID typeIdentifier);

	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition);

}
