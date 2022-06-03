package com.searchservice.app.rest.table.integration;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.searchservice.app.SearchServiceApplication;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.service.UserService;



@RunWith(SpringRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(classes =SearchServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ManageTableResourceIntegrationTest {

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();
	
	@Autowired
	ManageTableServicePort manageTableServicePort;
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;

	private String accessToken;
	
	@InjectMocks
	UserService userService;
	
	@Value("${base-token-url}")
	private String baseTokenUrl;
	
	private String tableName = "automatedTestCollection3";
	private int tenantId = 101;
	
	SchemaField search = new SchemaField("testField6", "string", true, true, false, true, true, false);
	
	List<SchemaField> attributes = new ArrayList<>(Arrays.asList(search));
	String expectedGetResponse = "{\n"
			  +"\"tableName\": \"gettingstarted3\",\n"
			  +"\"name\": \"default-config\",\n"
			  +"\"attributes\": [{\n"
		      +"\"name\": \"testField6\",\n"
		      +"\"type\": \"_nest_path_\",\n"
		      +"\"default_\": \"mydefault\",\n"
		      +"\"storable\": false,\n"
		      +"\"filterable\": true,"
		      +"\"required\": true,"
		      +"\"sortable\": true,\n"
		      +"\"multiValue\": true,\n"
		      +"}],\n"
		      +"\"statusCode\": 200\n"
		      +"}";
	@BeforeAll
	void getToken() {
		 headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		  HttpEntity<User> request = new HttpEntity<>(new User("admin","adminPassword@1"),headers);
		  ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
		response = restTemplate.postForEntity(baseTokenUrl, request, String.class);
		  JSONObject obj = new JSONObject(response.getBody());
		  accessToken = obj.getString("access_token");
	}
	

	
	@Order(1)
	@Test
	void testCreateTable()
	{
		CreateTable createTableDTO = new CreateTable(tableName, "B", attributes);
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<CreateTable> entity = new HttpEntity<>(createTableDTO, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + "/?tenantId=" + tenantId),
				 entity, String.class);
		int statusCode=200;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));	
	}
	
	@Order(9)
	@Test
	void tableInvalidName()
	{
		CreateTable createTableDTO = new CreateTable(tableName+"_1", "B", attributes);
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<CreateTable> entity = new HttpEntity<>(createTableDTO, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + "/?tenantId=" + tenantId),
				 entity, String.class);
		int statusCode=101;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(2)
	@Test
	void testGetTableInfo()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.GET, entity, String.class);
		 
		int statusCode=200;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(3)
	@Test
       void getCapacityPlans()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort("/api/v1" + "/manage/table" + "/capacity-plans"),
				HttpMethod.GET, entity, String.class);
		
		String expected = "{\"plans\":[{\"shards\":1,\"replicas\":1,\"name\":\"Basic\",\"sku\":\"B\"},{\"shards\":2,\"replicas\":2,\"name\":\"Standard\",\"sku\":\"S1\"},{\"shards\":3,\"replicas\":2,\"name\":\"Standard\",\"sku\":\"S2\"},{\"shards\":7,\"replicas\":3,\"name\":\"Standard\",\"sku\":\"S3\"},{\"shards\":5,\"replicas\":3,\"name\":\"Premium\",\"sku\":\"P\"}],\"message\":\"Successfully retrieved all Capacity Plans\",\"statusCode\":200}";
		
	assertEquals(expected,  new JSONObject(response.getBody()).toString());
		
	}
	
	@Order(4)
	@Test
	void getTableswithTenantId()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table/" + "/?tenantId=" + tenantId),
				HttpMethod.GET, entity, String.class);
		int statusCode=200;
		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	  @Order(5)
	@Test
	void testGetAllTables()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/all-tables" + "?pageNumber=1&pageSize=5"),
				HttpMethod.GET, entity, String.class);
	
        int statusCode=200;
		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
		
	
	}
	@Order(6)   
	@Test
	void updatetabletest()
	{
		ManageTable schemaDTO = new ManageTable(tableName, attributes);
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<ManageTable> entity = new HttpEntity<>(schemaDTO, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.PUT, entity, String.class);
		int statusCode=200;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	
		
	}
	  @Order(7)
	  @Test
	void deleteTable()
	{
			ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
			headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			HttpEntity<String> entity = new HttpEntity<String>(null, headers);
			ResponseEntity<String> response = restTemplate.exchange(
					createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
					HttpMethod.DELETE, entity, String.class);
			int statusCode=200;		
			assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
		
	}
	
	 @Order(10)
	@Test
	void creatingTableUnderDeletion()
	{
		CreateTable createTableDTO = new CreateTable(tableName, "B", attributes);
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<CreateTable> entity = new HttpEntity<>(createTableDTO, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + "/?tenantId=" + tenantId),
				 entity, String.class);
		int statusCode=107;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));	
	}
	@Order(8)
	@Test 
	void testGetAllTablesUnderDeletion()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table/" + "/deletion/all-tables" + "?pageNumber=1&pageSize=5"),
				HttpMethod.GET, entity, String.class);
		int statusCode=200;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
       
	}
 
	@Order(11)
	@Test
	void getschemaDeleteTable()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.GET, entity, String.class);
		 
		int statusCode=107;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(12)
	@Test
	void getSchemaTableNotFound()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName+"123" + "/?tenantId=" + tenantId),
				HttpMethod.GET, entity, String.class);
		 
		int statusCode=108;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(13)
	@Test
	void deleteTableNotFound()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName+"123"+ "/?tenantId=" + tenantId),
				HttpMethod.DELETE, entity, String.class);
		int statusCode=108;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(14)
	@Test
	void testTableUnderDeletion()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.DELETE, entity, String.class);
		int statusCode=107;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	
	@Order(15)
	@Test
	void schemaUnderDeletion()
	{
		ManageTable schemaDTO = new ManageTable(tableName, attributes);
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<ManageTable> entity = new HttpEntity<>(schemaDTO, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.PUT, entity, String.class);
		int statusCode=107;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
	@Order(17)
	@Test
	void restoreTestTableUnderDeletion()
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/restore/" + "/" + tableName + "?tenantId=" + tenantId),
				HttpMethod.PUT, entity, String.class);
		int statusCode=109;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	}
    @Order(16)
	@Test
	void testUndoDeleteTable() 
	{
		ReflectionTestUtils.setField(userService,"baseTokenUrl",baseTokenUrl);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint + "/manage/table" + "/restore/" + "/" + tableName + "?tenantId=" + tenantId),
				HttpMethod.PUT, entity, String.class);
		int statusCode=200;		
		assertEquals(statusCode,  new JSONObject(response.getBody()).getInt("statusCode"));
	
	}
	
	
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
	//@Order(12)
	@AfterAll
	void after()
	{
		manageTableServicePort.deleteTable(tableName+"_"+tenantId);
	}
	
}
