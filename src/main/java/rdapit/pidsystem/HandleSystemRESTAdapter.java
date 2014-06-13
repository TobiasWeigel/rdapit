package rdapit.pidsystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.internal.util.Base64;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import rdapit.PID;
import rdapit.PIDRecord;
import rdapit.Property;
import rdapit.typeregistry.PropertyDefinition;

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
	public Property<?> queryProperty(PID pid, PropertyDefinition propertyDefinition) throws IOException {
		
		String pidResponse = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName()).queryParam("type", propertyDefinition.getIdentifier().getIdentifierName()).request(MediaType.APPLICATION_JSON).get(String.class);
		// extract the Handle value data entry from the json response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(pidResponse);
		JsonNode values = rootNode.get("values");
		if (!(values.isArray() && values.size() > 0)) throw new IllegalStateException("values must be an array with at least one element");
		if (values.size() > 1) {
			// More than one property stored at this record
			throw new IllegalStateException("PID records with more than one property of same type are not supported yet");
		}
		String value = values.get(0).get("data").get("value").asText();
		// Now generate the property. The property name and value type are already given in its definition. 
		return propertyDefinition.generateProperty(value);
		
	}

	@Override
	public Property<?> queryProperty(PID pid, String propertyName) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	public PIDRecord queryPIDRecord(PID pid) throws JsonParseException, JsonMappingException, IOException {
		String response = individualHandleTarget.resolveTemplate("handle", pid.getIdentifierName()).request(MediaType.APPLICATION_JSON).get(String.class);
		System.out.println(response);
		return PIDRecord.fromJson(response);
	}

	protected URI baseURI;
	protected String authInfo;
	protected Client client;

	protected WebTarget rootTarget;
	protected WebTarget handlesTarget;
	protected WebTarget individualHandleTarget;

	public HandleSystemRESTAdapter(String baseURI, String userName, String userPassword) {
		super();
		this.baseURI = UriBuilder.fromUri(baseURI).path("api").build();
		try {
			this.authInfo = Base64.encodeAsString(URLEncoder.encode(userName, "UTF-8") + ":" + userPassword);
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
		PropertyDefinition propertyDef = new PropertyDefinition(new PID("11043.4/TYPE_TITLE"), "Title", new PID("String"));
		HandleSystemRESTAdapter hsra = new HandleSystemRESTAdapter("https://75.150.60.33:8006", "300:11053.4/admin", "password");
		boolean b = hsra.isIdentifierRegistered(new PID("11043.4/weigel_TEST1"));
		System.out.println(b);
		Property<?> pidr = hsra.queryProperty(new PID("11043.4/WEIGEL_TEST1"), propertyDef);
		System.out.println(pidr.getKey()+": "+pidr.getValueType()+": "+pidr.getValue());
	}

}
