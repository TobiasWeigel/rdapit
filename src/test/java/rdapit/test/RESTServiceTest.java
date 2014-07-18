package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import rdapit.pitservice.PITApplication;
import rdapit.typeregistry.PropertyDefinition;

public class RESTServiceTest extends JerseyTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleResolve() {
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		Response resp = rootTarget.path("test").request().get();
		assertEquals(200, resp.getStatus());
		// Some postings indicate that Tomcat may have a problem with encoded
		// slashes.
		// There's a solution however: in setenv include
		// CATALINA_OPTS="-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"
		resp = propertyResolveTarget.resolveTemplate("id", PropertyDefinition.IDENTIFIER_PIT_MARKER_PROPERTY).request().get();
		assertEquals(200, resp.getStatus());
		PropertyDefinition propDef = resp.readEntity(PropertyDefinition.class);
		assertEquals("pit.construct", propDef.getName());
		assertEquals(200, pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().head().getStatus());
		assertEquals(404, pidResolveTarget.resolveTemplate("id", "invalid_or_unknown_identifier").request().head().getStatus());
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		Map<String, String> pidrec = resp.readEntity(new HashMap<String, String>().getClass());
		assertEquals("http://www.example.com", pidrec.get("URL"));
	}

	@Override
	protected Application configure() {
		try {
			return new PITApplication();
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	}

}
