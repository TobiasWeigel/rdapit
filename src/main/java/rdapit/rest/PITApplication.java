package rdapit.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.glassfish.jersey.server.ResourceConfig;

import rdapit.pidsystem.HandleSystemRESTAdapter;
import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pitservice.TypingService;
import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.TypeRegistry;

public class PITApplication extends ResourceConfig {
	
	public PITApplication() throws IOException {
		super();
		packages("rdapit.rest");
		
		/* TODO: review this for production env. The property file should be outside the jar. */
		Properties properties = new Properties();
		InputStream propIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("pitapi.properties");
		if (propIS == null)
			throw new IOException("Property file pitapi.properties not found on classpath!");
		properties.load(propIS);
		
		IIdentifierSystem identifierSystem = new HandleSystemRESTAdapter(properties.getProperty("pidsystem.handle.baseURI"),properties.getProperty("pidsystem.handle.userName"), properties.getProperty("pidsystem.handle.userPassword"), properties.getProperty("pidsystem.handle.generatorPrefix"));
		ITypeRegistry typeRegistry = new TypeRegistry(properties.getProperty("typeregistry.baseURI"));
		TypingService typingService = new TypingService(identifierSystem, typeRegistry);
		ApplicationContext appContext = new ApplicationContext(typingService);
	}
	
	
}
