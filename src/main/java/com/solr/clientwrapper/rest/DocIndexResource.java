package com.solr.clientwrapper.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.web.JsonPath;
import org.springframework.expression.spel.ast.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.solr.clientwrapper.domain.service.DocumentIndexService;
import com.solr.clientwrapper.infrastructure.entity.DocumentIndex;
import com.solr.clientwrapper.infrastructure.entity.Employee;
import com.solr.clientwrapper.infrastructure.repository.DocIndexRepo;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/file")
public class DocIndexResource {

	@Autowired
	private SolrClient solrClient;
	private final static String solrDataName = "user";

	String urlString = "http://localhost:8983/Solr/user";
	SolrClient Solr = new HttpSolrClient.Builder(urlString).build();


	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<Object> fileUpload(@RequestBody MultipartFile file,HttpServletRequest request) throws IOException, JsonException, ParseException{
			 
			  JSONParser jsonP = new JSONParser();
			  SolrInputDocument doc = new SolrInputDocument(); 
			  
			  try(FileReader reader = new FileReader("C:\\Users\\user\\Pictures\\Saved Pictures\\books.json")){
			   //Read JSON File
				  System.out.println(reader);
				  JsonObject obj = (JsonObject) Jsoner.deserialize(reader);
				  System.out.println("obje="+obj);
				
					/*
					 * List<DocumentIndex> employees = new ArrayList<>(); employees.add(new
					 * DocumentIndex(1,"Ravi","Pilgar")); repository.saveAll(docIndex);
					 */
				  
				  JsonArray booksList = (JsonArray) obj.get("books");
				  booksList.forEach(entry -> {
				        JsonObject books = (JsonObject) entry;
				      String name =  (String) books.get("name");
				      String author =  (String) books.get("author");
				    });
				  	
				 
				  
				  //JSONArray empList = (JSONArray) obj;
			   //Iterate over emp array
			  // empList.forEach(emp ->parseEmpObj((JSONObject)emp));
			   
			  }
			 
			  catch (FileNotFoundException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
			  return new ResponseEntity<>(file.getBytes(), HttpStatus.OK);
			 }
			 private static void parseEmpObj(JSONObject emp) {
			  JSONObject empObj = (JSONObject) emp.get("books");
			  String fname = (String) empObj.get("name");
			  String lname = (String) empObj.get("author");
			 		
			 }
			 
			  @PostMapping("/files")
			  public void streamFile(@RequestParam("file") MultipartFile file) {

				  Response response = RestAssured.get("C:\\Users\\user\\Pictures\\Saved Pictures\\books.json");
				  System.out.println("response"+response);
				  Object responseAsObject = response.getClass();
							if(responseAsObject instanceof List)
							{
								List responseAsList = (List)responseAsObject;
								System.out.println(responseAsList.size());
							}
							else if(responseAsObject instanceof Map)
							{
								Map responseAsMap = (Map)responseAsObject;
								System.out.println(((Map) responseAsObject).keySet());
							}
							
			  }
			  
			  @RequestMapping(
					    value = "/process", 
					    method = RequestMethod.POST)
					public void process(@RequestBody Map<String, Object>[] payload) 
					    throws Exception {

					    System.out.println(payload);

					}
			  
			  @RequestMapping(
					    value = "/process1", 
					    method = RequestMethod.POST,
					    consumes = "application/json",
			  			produces="application/json")
					public String process(@RequestBody String payload) throws Exception {

				  String urlString = "http://localhost:8983/solr/user";
					SolrClient Solr = new HttpSolrClient.Builder(urlString).build();
					SolrInputDocument doc = new SolrInputDocument();
					 
					 JsonObject obj = (JsonObject) Jsoner.deserialize(payload);
					 System.out.println("obj"+obj);
					 
					 JsonArray booksList = (JsonArray) obj.get("books");
					  booksList.forEach(entry -> {
					        JsonObject books = (JsonObject) entry;
					      String name =  (String) books.get("name");
					      String author =  (String) books.get("author");
					      
					      //Adding fields to the document 
					      doc.addField("name", name); 
					      doc.addField("author", author); 
					    });
					 
					
					
					
					//Adding the document to Solr 
				      Solr.add(doc);         
				         
				      //Saving the changes 
				      Solr.commit(); 
					return payload;					  
					  
					}
}







	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
/*
 * 
 * try {
 * 
 * // create a reader Reader reader = Files.newBufferedReader(Paths.
 * get("C:\\Users\\user\\Pictures\\Saved Pictures\\books.json"));
 * 
 * // create parser JsonObject parser = (JsonObject) Jsoner.deserialize(reader);
 * 
 * 
 * System.out.println("Object " +parser);
 * 
 * 
 * } catch (Exception ex) { ex.printStackTrace(); } return new
 * ResponseEntity<>(file.getBytes(), HttpStatus.OK);
 * 
 * }
 * 
 */








 