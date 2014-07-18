package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import rdapit.pitservice.PITApplication;
import rdapit.typeregistry.PropertyDefinition;

public class RESTServiceTest extends JerseyTest {

	@Test
	public void testSimpleResolve() {
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		Response resp = rootTarget.path("test").request().get();
		assertEquals(200, resp.getStatus());
		// Some postings indicate that Tomcat may have a problem with encoded slashes.
		// There's a solution however: in setenv include
		// CATALINA_OPTS="-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true" 
		assertEquals(200, propertyResolveTarget.resolveTemplate("id", PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY).request().get().getStatus());
	}
	
	@Override
	protected Application configure() {
		try {
			return new PITApplication();
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	} 

/*	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
	} */
}
