package rdapit.pidsystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
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

import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;
import rdapit.typeregistry.TypeRegistry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

	@Override
	public boolean isIdentifierRegistered(PID pid) {
		Response response = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName()).request(MediaType.APPLICATION_JSON).head();
		return response.getStatus() == 200;
	}

	@Override
	public String queryProperty(PID pid, PropertyDefinition propertyDefinition) throws IOException {

		String pidResponse = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName())
				.queryParam("type", propertyDefinition.getIdentifier().getIdentifierName()).request(MediaType.APPLICATION_JSON).get(String.class);
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

	@Override
	public String queryProperty(PID pid, String propertyName, ITypeRegistry typeRegistry) throws IOException {
		// Retrieve property definition given the name
		List<PropertyDefinition> propertyDefinitions = typeRegistry.queryPropertyDefinitionByName(propertyName);
		if (propertyDefinitions.size() > 1) {
			throw new IllegalArgumentException("The given property name '" + propertyName + "' is not unique in the type registry");
		} else if (propertyDefinitions.isEmpty()) {
			throw new IllegalArgumentException("The given property name '" + propertyName + "' cannot be found in the type registry");
		}
		// Forward to other method
		return queryProperty(pid, propertyDefinitions.get(0));
	}

	public PIDRecord queryPIDRecord(PID pid) throws JsonParseException, JsonMappingException, IOException {
		String response = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName()).request(MediaType.APPLICATION_JSON).get(String.class);
		return PIDRecord.fromJson(response);
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

	public static void main(String[] args) throws Exception {
		// test read from registry and HS adapter
		TypeRegistry typeRegistry = new TypeRegistry("http://typeregistry.org/registrar");
		PropertyDefinition propertyDef = typeRegistry.queryPropertyDefinition(new PID("11314.2/07841c3f84cbe0d4ff8687d0028c2622"));
		System.out.println(propertyDef.getIdentifier() + ": " + propertyDef.getName() + ", value type: " + propertyDef.getValueType());
		HandleSystemRESTAdapter hsra = new HandleSystemRESTAdapter("https://75.150.60.33:8006", "300:11043.4/admin", "password", "11043.4");
		boolean b = hsra.isIdentifierRegistered(new PID("11043.4/weigel_TEST1"));
		System.out.println(b);
		String pidr = hsra.queryProperty(new PID("11043.4/WEIGEL_TEST2"), propertyDef);
		System.out.println(propertyDef.getName() + ": " + propertyDef.getValueType() + ": " + pidr);
		pidr = hsra.queryProperty(new PID("11043.4/WEIGEL_TEST2"), "Title", typeRegistry);
		System.out.println("Title: " + pidr);
		// test create PID
		HashMap<String, String> propMap = new HashMap<String, String>();
		propMap.put("author", "John D.");
		PID pid = hsra.registerPID(propMap);
		System.out.println("New PID registered: " + pid);

	}

	protected String generatePIDName() {
		String uuid = UUID.randomUUID().toString();
		return this.generatorPrefix + "/" + uuid;
	}

	@Override
	public PID registerPID(Map<String, String> properties) throws IOException {
		Response response;
		PID pid;
		do {
			pid = new PID(generatePIDName());
			PIDRecord pidRecord = new PIDRecord(pid);
			pidRecord.addProperties(properties);
			// PUT record to HS
			String jsonText = objectMapper.writeValueAsString(pidRecord);
			response = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName()).queryParam("overwrite", false)
					.request(MediaType.APPLICATION_JSON).header("Authorization", "Basic " + authInfo).put(Entity.json(jsonText));
			// status 409 is sent in case the Handle already exists
		} while (response.getStatus() == 409);
		// Evaluate response
		if (response.getStatus() == 201) {
			return pid;
		} else
			throw new IOException("Error trying to create PID " + pid);
	}

	@Override
	public Map<String, String> queryByType(PID pid, TypeDefinition typeDefinition) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public Map<String, String> queryByType(PID pid, PID typeIdentifier, ITypeRegistry typeRegistry) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void deletePID(PID pid) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented");
	}

}
