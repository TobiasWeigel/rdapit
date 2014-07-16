package rdapit.pitservice;

import java.io.IOException;
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
	public Object genericResolve(PID pid) throws IOException;

}