package rdapit.pitservice;

import java.util.Map;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pidsystem.PID;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

/**
 * Core interface for clients to contact. Implementations will provide the
 * necessary fixation in a concrete protocol (e.g. HTTP-REST).
 * 
 */
public interface ITypingService extends IIdentifierSystem {

	public Map<String, String> getAllProperties(PID pid);
	
	public PropertyDefinition describeProperty(PID propertyIdentifier);
	
	public TypeDefinition describeType(PID typeIdentifier);
	
	public boolean conformsToType(PID pid, PID typeIdentifier);
	
}