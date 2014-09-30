package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import rdapit.pidsystem.HandleSystemRESTAdapter;
import rdapit.pitservice.EntityClass;
import rdapit.pitservice.PIDInformation;
import rdapit.pitservice.TypingService;
import rdapit.rest.ApplicationContext;
import rdapit.rest.PITApplication;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeRegistry;

public class RESTServiceTest extends JerseyTest {
	
	private TypeRegistry typeRegistry;
	private HandleSystemRESTAdapter identifierSystem;

	@Test
	public void testResolve() {
		/* Prepare targets */
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget pidResolveTarget = rootTarget.path("pid").path("{id}");
		WebTarget pidResolveTarget2 = rootTarget.path("pid").path("{prefix}").path("{suffix}");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		WebTarget peekTarget = rootTarget.path("peek").path("{id}");
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
		PIDInformation pidrec = resp.readEntity(PIDInformation.class);
		assertEquals("http://www.example.com", pidrec.getPropertyValue("URL"));
		// same call but on the other target that does not encode the slash between prefix and suffix
		resp = pidResolveTarget2.resolveTemplate("prefix", "11043.4").resolveTemplate("suffix", "pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		pidrec = resp.readEntity(PIDInformation.class);
		assertEquals("http://www.example.com", pidrec.getPropertyValue("URL"));
		/* Query single property */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("filter_by_property", "11314.2/2f305c8320611911a9926bb58dfad8c9").request().get();
		assertEquals(200, resp.getStatus());
		/* Query property by type */
		resp = pidResolveTarget.resolveTemplate("id", "11043.4/pitapi_test1").queryParam("type", "11043.4/test_type").request().get();
		/* Query prop definition */
		assertEquals(200, propertyResolveTarget.resolveTemplate("id", "11314.2/56bb4d16b75ae50015b3ed634bbb519f").request().get().getStatus());
		/* Peek tests */
		resp = peekTarget.resolveTemplate("id", "11314.2/2f305c8320611911a9926bb58dfad8c9").request().get();
		assertEquals(200, resp.getStatus());
		assertEquals(EntityClass.PROPERTY, resp.readEntity(EntityClass.class));
		resp = peekTarget.resolveTemplate("id", "11043.4/pitapi_test1").request().get();
		assertEquals(200, resp.getStatus());
		assertEquals(EntityClass.OBJECT, resp.readEntity(EntityClass.class));
	}

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
			assertEquals(response.getStatus(), 200);
			PIDInformation pidinfo = response.readEntity(PIDInformation.class);
			assertEquals(pidinfo.getPropertyValue("URL"), properties.get("URL"));
		} finally {
			/* Delete PID */
			response = pidResolveTarget.resolveTemplate("id", pid).request().delete();
			assertEquals(200, response.getStatus());
		}
	}
	
	@Override
	protected Application configure() {
		try {
			identifierSystem = new HandleSystemRESTAdapter("https://75.150.60.33:8006", "300:11043.4/admin", "password", "11043.4");
			typeRegistry = new TypeRegistry("http://38.100.130.13:8002/registrar", "11314.2");
			new ApplicationContext(new TypingService(identifierSystem, typeRegistry));
			return new PITApplication();
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	}

}
