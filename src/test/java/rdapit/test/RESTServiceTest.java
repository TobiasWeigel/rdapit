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

import rdapit.pidsystem.HandleSystemRESTAdapter;
import rdapit.pidsystem.IIdentifierSystem;
import rdapit.pitservice.TypingService;
import rdapit.rest.ApplicationContext;
import rdapit.rest.PITApplication;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeRegistry;

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
		assertEquals(200, resp.getStatus());
		// Some postings indicate that Tomcat may have a problem with encoded
		// slashes.
		// There's a solution however: in setenv include
		// CATALINA_OPTS="-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"
		assertEquals(200, pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().head().getStatus());
		assertEquals(200, pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().get().getStatus());
		assertEquals(404, pidResolveTarget.resolveTemplate("id", "11043.4/invalid_or_unknown_identifier").request().head().getStatus());
		assertEquals(404, pidResolveTarget.resolveTemplate("id", "11043.4/invalid_or_unknown_identifier").request().get().getStatus());
		/* Query full record */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		Map<String, String> pidrec = resp.readEntity(new HashMap<String, String>().getClass());
		assertEquals("http://www.example.com", pidrec.get("URL"));
		/* Query single property (by property name) */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("property", "LICENSE").request().get();
		assertEquals(200, resp.getStatus());
		/* Query property by type */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("type", "11043.4/test_type").request().get();
		/* Query prop definition */
		assertEquals(200, propertyResolveTarget.resolveTemplate("id", "11314.2/56bb4d16b75ae50015b3ed634bbb519f").request().get().getStatus());
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
			IIdentifierSystem ids = new HandleSystemRESTAdapter("https://75.150.60.33:8006", "300:11043.4/admin", "password", "11043.4");
			TypeRegistry tr = new TypeRegistry("http://typeregistry.org/registrar");
			new ApplicationContext(new TypingService(ids, tr));
			return new PITApplication();
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	}

}
