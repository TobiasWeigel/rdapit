package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
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
	public void testResolve() {
		/* Prepare targets */
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		/* Simple tests */
		Response resp = rootTarget.path("ping").request().get();
		assertEquals(204, resp.getStatus());
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
		/* Query full record */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		Map<String, String> pidrec = resp.readEntity(new HashMap<String, String>().getClass());
		assertEquals("http://www.example.com", pidrec.get("URL"));
		/* Query single property (by property name) */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("property", "URL").request().get();
		assertEquals(404, resp.getStatus()); // will return a 404 because URL is
												// not a registered property..
		/* Query property by type */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("type", "11043.4/test_type").request().get();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateDeletePID() {
		/* Prepare targets */
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		/* Create PID */
		HashMap<String, String> properties = new HashMap<>();
		properties.put("URL", "http://www.eudat.eu");
		Response response = rootTarget.path("pid").request().post(Entity.json(properties));
		assertEquals(201, response.getStatus());
		String pid = response.readEntity(String.class);
		System.out.println("PID created: " + pid);
		try {
			/* Read properties */
			response = pidResolveTarget.resolveTemplate("id", pid).request().get();
			Map<String, String> readProps = response.readEntity(new HashMap<String, String>().getClass());
			assertEquals(readProps.get("URL"), properties.get("URL"));
		} finally {
			/* Delete PID */
			response = pidResolveTarget.resolveTemplate("id", pid).request().delete();
			assertEquals(200, response.getStatus());
		}
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
