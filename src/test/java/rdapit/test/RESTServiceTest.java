package rdapit.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.Test;

import rdapit.pitservice.PITApplication;

public class RESTServiceTest extends JerseyTest {

	/**
	 * Simple test container to load the servlet-based Grizzly container.
	 * Configuration via pom appears to be broken, thus the need for an extra
	 * class.
	 * 
	 * @author Tobias Weigel
	 * 
	 */
	private static class SimpleTestContainer implements TestContainer {
		private HttpServer server;
		private URI baseUri;

		public SimpleTestContainer(URI baseUri) {
			this.baseUri = baseUri;
		}

		@Override
		public ClientConfig getClientConfig() {
			return null;
		}

		@Override
		public URI getBaseUri() {
			return baseUri;
		}

		@Override
		public void start() {
			try {
				this.server = GrizzlyWebContainerFactory.create(baseUri, Collections.singletonMap("jersey.config.server.provider.packages", "rdapit"));
			} catch (ProcessingException e) {
				throw new TestContainerException(e);
			} catch (IOException e) {
				throw new TestContainerException(e);
			}
		}

		@Override
		public void stop() {
			this.server.shutdownNow();
		}
	}

	private static class SimpleTestContainerFactory implements TestContainerFactory {
		@Override
		public TestContainer create(final URI baseUri, DeploymentContext context) throws IllegalArgumentException {
			return new SimpleTestContainer(baseUri);
		}
	}

	@Test
	public void testSimpleResolve() {
		URI baseURI = UriBuilder.fromUri(this.getBaseUri()).build();
		WebTarget rootTarget = client().target(baseURI).path("pitapi");
		WebTarget propertyResolveTarget = rootTarget.path("property").path("{id}");
		Response resp = rootTarget.path("test").request().get();
		assertEquals(200, resp.getStatus());
		// This appears to be working. however introducing a slash in the request parameter causes a 500...
		assertEquals(200, propertyResolveTarget.resolveTemplate("id", "hello").request().get().getStatus());
	}
	
	@Override
	protected Application configure() {
		try {
			return new PITApplication();
		} catch (Exception exc) {
			throw new IllegalStateException("Could not initialize application: ", exc);
		}
	} 

	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		/*
		 * Could also use new GrizzlyWebTestContainerFactory(), however that
		 * produces a strange exception ("The deployment context must be an
		 * instance of ServletDeploymentContext.")
		 */
		return new SimpleTestContainerFactory();
	}
}
