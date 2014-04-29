package rdapit.typeregistry;

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
	
	public void createPropertyDefinition(PID propertyIdentifier, PropertyDefinition propertyDefinition);
	
	public TypeDefinition queryTypeDefinition(PID typeIdentifier);

	public void createTypeDefinition(PID typeIdentifier, TypeDefinition typeDefinition);

}
