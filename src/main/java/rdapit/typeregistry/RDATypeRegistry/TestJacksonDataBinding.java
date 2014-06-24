/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main.java.rdapit.typeregistry.RDATypeRegistry;

import main.java.rdapit.typeregistry.RDATypeRegistry.RDAType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
 
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJacksonDataBinding {
    
     public static void main(String[] args) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
        String url = "http://typeregistry.org/registrar/records/11314.2/0aecc18032a27d5e0b242fcd31cc2e72";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        RDAType trt = mapper.readValue(new URL(url), RDAType.class);
        
         System.out.println(trt.toString());
        
        
        
        //Albums albums = mapper.readValue(new URL(url), Albums.class);
        //Dataset[] datasets = albums.getDataset();
        //for (Dataset dataset : datasets) {
        //    System.out.println(dataset.getAlbum_title());
        //}
    }
    
}
