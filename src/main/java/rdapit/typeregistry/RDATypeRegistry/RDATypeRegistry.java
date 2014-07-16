package rdapit.typeregistry.RDATypeRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import rdapit.typeregistry.ITypeRegistry;
import rdapit.typeregistry.PropertyDefinition;
import rdapit.typeregistry.TypeDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RDATypeRegistry implements ITypeRegistry {

    private String TRUrl;

    public RDATypeRegistry(String TRUrl) {
        this.TRUrl = TRUrl;
    }

    public RDAType read(String id) {

        RDAType trt = new RDAType();
        try {

            // --- Getting the Json stuff from TR:
            URL TypeRegistryURL = new URL(this.TRUrl + id);
            URLConnection urlc = TypeRegistryURL.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

            StringBuilder sb = new StringBuilder("");
            String inputLine = "";

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
                sb.append("\n");
            }
            in.close();

            // --- Parsing the Json data into a TRType object:
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(sb.toString());

            trt.setStatus(node.get("status").textValue());                        
            trt.setCode(node.get("code").asInt());
            
            JsonNode data = node.get("extras").get("data");
            
            trt.setId(data.get("ID").asText());
            trt.setExplanationOfUse(data.get("explanation_of_use").asText());
            trt.setHumanDescription(data.get("human_description").asText());
            trt.setModel(data.get("model").asText());
            
            JsonNode provenance = data.get("provenance");
            
            Iterator it = provenance.fieldNames();
            
            while(it.hasNext()){
                String name = (String)it.next();              
                String value = provenance.get(name).asText();                
                trt.getProvenance().put(name, value);
            }
            
            JsonNode keyValue = data.get("key_value");
            
            it = keyValue.elements();
            
            while(it.hasNext()){
                JsonNode n = (JsonNode)it.next();
                String name = n.get("key").asText();
                String value = n.get("val").asText();
                trt.getPairs().put(name, value);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(RDATypeRegistry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RDATypeRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return trt;
    } // read

    public static void main(String[] args) {
        RDATypeRegistry trh = new RDATypeRegistry("http://typeregistry.org/registrar/records/");
        RDAType trt = trh.read("11314.2/0aecc18032a27d5e0b242fcd31cc2e72");
        System.out.println(trt.toString());
    }

    @Override
    public PropertyDefinition queryPropertyDefinition(String propertyIdentifier) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<PropertyDefinition> queryPropertyDefinitionByName(String propertyName) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createPropertyDefinition(PropertyDefinition propertyDefinition) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removePropertyDefinition(String propertyIdentifier) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TypeDefinition queryTypeDefinition(String typeIdentifier) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createTypeDefinition(String typeIdentifier, TypeDefinition typeDefinition) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public Object query(String identifier) {
        throw new UnsupportedOperationException("Not supported yet."); 
	}

}
