package rdapit.pidsystem;

import rdapit.PID;
import rdapit.Property;

/**
 * Main abstraction interface towards the identifier system containing
 * registered identifiers and associated state information.
 * 
 */
public interface IIdentifierSystem {
	
	public boolean isIdentifierRegistered(PID pid);
	
	public Property<?> queryProperty(PID pid, PID propertyIdentifier);
	
	public Property<?> queryProperty(PID pid, String propertyName);
	
}
