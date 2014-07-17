package rdapit.pitservice;

import org.apache.log4j.Logger;

import rdapit.pidsystem.IIdentifierSystem;
import rdapit.typeregistry.ITypeRegistry;

/**
 * Singleton.
 */
public class ApplicationContext {

	public static ApplicationContext instance;
	private IIdentifierSystem identifierSystem;
	private ITypeRegistry typeRegistry;

	private static final Logger logger = Logger.getLogger(ApplicationContext.class);

	private ApplicationContext() {
		instance = this;
		// TODO: create instances of identifier system and type registry
	}

	public ApplicationContext(IIdentifierSystem identifierSystem, ITypeRegistry typeRegistry) {
		instance = this;
		this.typeRegistry = typeRegistry;
		this.identifierSystem = identifierSystem;
	}

	public static ApplicationContext getInstance() {
		if (ApplicationContext.instance == null)
			try {
				return new ApplicationContext();
			} catch (Exception exc) {
				logger.error(exc.getMessage(), exc);
			}
		return instance;
	}
	
	public IIdentifierSystem getIdentifierSystem() {
		return identifierSystem;
	}
	
	public ITypeRegistry getTypeRegistry() {
		return typeRegistry;
	}
}
