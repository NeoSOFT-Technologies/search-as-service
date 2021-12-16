package com.solr.clientwrapper.rest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.xdevapi.JsonArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;




@RestController
@RequestMapping("/file")
public class SolrDocParseResource {

	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public String sendjson(@RequestBody String payload) throws Exception {

		// String urlString = "http://localhost:8983/solr/user";
		String url = "http://localhost:8983/solr/docparse/schema/fields?wt=json";
		// SolrClient Solr = new HttpSolrClient.Builder(urlString).build();
		// SolrInputDocument doc = new SolrInputDocument();

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		 con.setRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//System.out.println(response.toString());
		
		org.json.JSONObject jsonorg = new org.json.JSONObject(response.toString());
		
		 // JsonObject obj1 = (JsonObject) Jsoner.deserialize(response.toString());
		 // System.out.println("obj" + obj1);
		  
		 // JsonArray jsonArray = (JsonArray) obj1.get("fields");
		org.json.JSONArray jsonArray = (org.json.JSONArray) jsonorg.get("fields");
		  System.out.println("feild :-" + jsonArray);
		  
		  JSONArray jsonfields = new JSONArray();         
	     

		  for(int n = 0; n < jsonArray.length(); n++)
		  {
		      JSONObject object = jsonArray.getJSONObject(n);
		      if (object.has("required")) {
		    	  jsonfields.put(object);
		    	 // map = mapper.readValue(object.toString(), new TypeReference<Map<String, Object>>(){});
		    	 // map = mapper.readValues(object.get("name").toString(), object);
		    	  //System.out.println("map-" + jsonfields);
		    	}          
		      
		  }
		  HashMap<String,Boolean> acctyp = new HashMap<String,Boolean>();
		  HashMap<String, HashMap<String,Boolean>> gens = new HashMap<String,HashMap<String,Boolean>>();
		 
		  for(int i = 0; i < jsonfields.length(); i++) {
			    JSONObject tagObject = jsonfields.getJSONObject(i);
			    
			    String mandatory_tag = tagObject.getString("name");
			    String id = tagObject.getString("type");
			    Boolean multivalue = tagObject.getBoolean("multiValued");
			    gens.put(mandatory_tag, acctyp);
			    if(gens.containsKey(mandatory_tag)) {			    	
			    	acctyp.put(id, multivalue);			    	
			        // arrayID.add(id);
			    } else {		    	
					
			       
			    }
		  }
		  System.out.println("valid json :-" + jsonfields);
		  System.out.println("hashmap :-" + gens.keySet());
		  
		  
		  // Input jason for comparision
		  org.json.JSONObject responsejson = new org.json.JSONObject(payload);
		  System.out.println("keysets :-" + responsejson.keySet());
		  
		  JSONArray jArray = new JSONArray();
		  for (@SuppressWarnings("rawtypes")
		Iterator iterator = responsejson.keySet().iterator(); iterator.hasNext();) {
			  String key = (String) iterator.next();
			  jArray = (JSONArray) responsejson.get(key);
			  
			  }
		  System.out.println("input array :-" + jArray);
		//  JSONArray ary = (JSONArray) obj1.get("fields");  
		Integer size =0;
	
		  for(int n = 0; n < jArray.length(); n++)
		  {
		      JSONObject inputobject = jArray.getJSONObject(n);	
		      
		      for(int k = 0; k < gens.size(); k++)
			  {
		    	  for (@SuppressWarnings("rawtypes")
		  		Iterator iterat = gens.keySet().iterator(); iterat.hasNext();) {
		    		  ArrayList al = new ArrayList();    		  
		  			  al.add(iterat.next());		  			
		  			HashMap<String, Boolean> iterat2 =  gens.values().iterator().next();
	  				Iterator valuekey=iterat2.keySet().iterator(); iterat.hasNext();
		  			String key2 = (String) valuekey.next();
		  			
		  			System.out.println("datatype from map -"+key2);
		  			
		  			 if(inputobject.has(gens.keySet().toString())) {	  				 
		  				System.out.println("tr5ue");			 
		  				 
		  				
		  			 }else		
		  				System.out.println("false");		  				
		  			  }
		    	 
			  }	    	       
		      
		  }
		 
			
	     
		return payload;

		/*
		 * Set<String> keys =obj.keySet(); for(String key:keys) {
		 * System.out.println("Key :: "+key +", Value :: "+obj.get(key));
		 * doc.addField(key, obj.get(key)); } // Adding the document to Solr
		 * Solr.add(doc);
		 * 
		 * // Saving the changes Solr.commit(); return payload;
		 */
	}

}
