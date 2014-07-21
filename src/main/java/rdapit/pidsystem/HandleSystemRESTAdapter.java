package rdapit.pidsystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.internal.util.Base64;

import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Concrete adapter of an identifier system that connects to the Handle System
 * through its native REST interface available from HS v8 on.
 * 
 */
public class HandleSystemRESTAdapter implements IIdentifierSystem {

	public static final boolean UNSAFE_SSL = true;

	private final static class TrustAllX509TrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
		}

	}

	protected URI baseURI;
	protected String authInfo;
	protected Client client;
	protected String generatorPrefix;

	private ObjectMapper objectMapper = new ObjectMapper();

	protected WebTarget rootTarget;
	protected WebTarget handlesTarget;
	protected WebTarget individualHandleTarget;

	public HandleSystemRESTAdapter(String baseURI, String userName, String userPassword, String generatorPrefix) {
		super();
		this.generatorPrefix = generatorPrefix;
		this.baseURI = UriBuilder.fromUri(baseURI).path("api").build();
		try {
			this.authInfo = Base64.encodeAsString(URLEncoder.encode(userName, "UTF-8") + ":" + URLEncoder.encode(userPassword, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Error while encoding the user name in UTF-8", e);
		}

		if (UNSAFE_SSL) {
			/* TODO: REMOVE THIS IN PRODUCTION VERSION! */
			try {
				SSLContext sslContext;
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] { new TrustAllX509TrustManager() }, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String string, SSLSession ssls) {
						return true;
					}
				});
				this.client = ClientBuilder.newBuilder().sslContext(sslContext).build();
			} catch (NoSuchAlgorithmException | KeyManagementException e) {
				throw new IllegalStateException("Could not initialize unsafe SSL constructs", e);
			}
		} else {
			this.client = ClientBuilder.newBuilder().build();
		}
		this.rootTarget = client.target(baseURI).path("api");
		this.handlesTarget = rootTarget.path("handles");
		this.individualHandleTarget = handlesTarget.path("{handle}");
	}

	@Override
	public boolean isIdentifierRegistered(String pid) {
		Response response = individualHandleTarget.resolveTemplate("handle", pid).request(MediaType.APPLICATION_JSON).head();
		return response.getStatus() == 200;
	}

	@Override
	public String queryProperty(String pid, PropertyDefinition propertyDefinition) throws IOException {
		String pidResponse = individualHandleTarget.resolveTemplate("handle", pid).queryParam("type", propertyDefinition.getIdentifier())
				.request(MediaType.APPLICATION_JSON).get(String.class);
		// extract the Handle value data entry from the json response
		JsonNode rootNode = objectMapper.readTree(pidResponse);
		JsonNode values = rootNode.get("values");
		if (!values.isArray())
			throw new IllegalStateException("Invalid response format: values must be an array");
		if (values.size() == 0)
			return null;
		if (values.size() > 1) {
			// More than one property stored at this record
			throw new IllegalStateException("PID records with more than one property of same type are not supported yet");
		}
		String value = values.get(0).get("data").get("value").asText();
		return value;

	}

	protected String generatePIDName() {
		String uuid = UUID.randomUUID().toString();
		return this.generatorPrefix + "/" + uuid;
	}

	@Override
	public String registerPID(Map<String, String> properties) throws IOException {
		Response response;
		String pid = generatePIDName();
		do {
			// PUT record to HS
			Collection<Map<String, String>> record = new LinkedList<Map<String, String>>();
			int idx = 0;
			for (String key : properties.keySet()) {
				idx += 1;
				Map<String, String> handleValue = new HashMap<String, String>();
				handleValue.put("index", "" + idx);
				handleValue.put("type", key);
				handleValue.put("data", properties.get(key));
				record.add(handleValue);
			}
			String jsonText = objectMapper.writeValueAsString(record);
			response = individualHandleTarget.resolveTemplate("handle", pid).queryParam("overwrite", false).request(MediaType.APPLICATION_JSON)
					.header("Authorization", "Basic " + authInfo).put(Entity.json(jsonText));
			// status 409 is sent in case the Handle already exists
		} while (response.getStatus() == 409);
		// Evaluate response
		if (response.getStatus() == 201) {
			return pid;
		} else
			throw new IOException("Error trying to create PID " + pid);
	}

	@Override
	public Map<String, String> queryByType(String pid, TypeDefinition typeDefinition) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public boolean deletePID(String pid) {
		Response response = individualHandleTarget.resolveTemplate("handle",  pid).request().header("Authorization",  "Basic "+authInfo).delete();
		return response.getStatus() == 200; 
	}

	@Override
	public boolean isTypeRegistryPID(String pid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, String> queryAllProperties(String pid) throws IOException {
		String pidResponse = individualHandleTarget.resolveTemplate("handle", pid).request(MediaType.APPLICATION_JSON).get(String.class);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(pidResponse);
		Map<String, String> result = new HashMap<>();
		for (JsonNode valueNode : root.get("values")) {
			if (!(valueNode.get("data").get("format").asText().equals("string") || valueNode.get("data").get("format").asText().equals("base64") || valueNode.get("data")
					.get("format").asText().equals("hex")))
				continue;
			// index is ignored..
			result.put(valueNode.get("type").asText(), valueNode.get("data").get("value").asText());
		}
		return result;
	}
}
