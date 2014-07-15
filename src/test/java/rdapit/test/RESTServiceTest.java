package rdapit.test;

import static org.junit.Assert.*;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import rdapit.pitservice.TypingRESTServer;

public class RESTServiceTest extends JerseyTest {

	@Test
	public void testSimpleResolve() {
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		Client client = ClientBuilder.newBuilder().build();
		assertEquals(200, client.target(baseURI).path("pitapi").path("resolve").request().get().getStatus());

	}

	@Override
	protected Application configure() {
		return new ResourceConfig(TypingRESTServer.class);
	}

}
