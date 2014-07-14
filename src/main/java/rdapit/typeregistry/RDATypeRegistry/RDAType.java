package rdapit.typeregistry.RDATypeRegistry;

import java.util.HashMap;
import java.util.Map;

public class RDAType {

    private String status;
    private int code;
    private HashMap<String, String> provenance;
    private String model;
    private HashMap<String, String> pairs;
    private String id;
    private String explanationOfUse;
    private String humanDescription;

    public RDAType() {
        this.status = "";       
        this.code = -1;
        this.model = "";
        this.id = "";
        this.explanationOfUse = "";
        this.humanDescription = "";
        this.provenance = new HashMap<>();
        this.pairs = new HashMap<>();                                
    }

    @Override
    public String toString() {
        String temp = "";

        temp = temp + "Status: " + status + "\n";
        temp = temp +"Code: " + code + "\n";
        temp = temp +"Model: " + model + "\n";
        temp = temp +"id: " + id + "\n";
        temp = temp +"explanationOfUse: " + explanationOfUse + "\n";
        temp = temp +"humanDescription: " + humanDescription + "\n";

        temp = temp + "---Provenance:---\n";
        for (Map.Entry kv : provenance.entrySet()) {
            temp = temp + kv.getKey() + ": " + kv.getValue() + "\n";
        }
        
        temp = temp + "---Data:---\n";
        for (Map.Entry kv : pairs.entrySet()) {
            temp = temp + kv.getKey() + ": " + kv.getValue() + "\n";
        }
        return temp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public HashMap<String, String> getProvenance() {
        return provenance;
    }

    public void setProvenance(HashMap<String, String> provenance) {
        this.provenance = provenance;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public HashMap<String, String> getPairs() {
        return pairs;
    }

    public void setPairs(HashMap<String, String> pairs) {
        this.pairs = pairs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExplanationOfUse() {
        return explanationOfUse;
    }

    public void setExplanationOfUse(String explanationOfUse) {
        this.explanationOfUse = explanationOfUse;
    }

    public String getHumanDescription() {
        return humanDescription;
    }

    public void setHumanDescription(String humanDescription) {
        this.humanDescription = humanDescription;
    }

}
