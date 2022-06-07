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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import com.searchservice.app.SearchServiceApplication;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.service.UserService;
import com.searchservice.app.rest.errors.HttpStatusCode;

@RunWith(SpringRunner.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(
        properties = {
                "username: admin",
                "password: adminPassword@1"
        }
)
@SpringBootTest(classes =SearchServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ManageTableResourceIntegrationTest {

	@LocalServerPort
	private int port;

	@Value("${username}")
	private String username;
	
	@Value("${password}")
	private String password;

	TestRestTemplate restTemplate = new TestRestTemplate();

	HttpHeaders headers = new HttpHeaders();
	
	@Autowired
	ManageTableServicePort manageTableServicePort;
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	
	private static final String MANAGE_TABLE = "/manage/table";
	private static final String STATUS_CODE = "statusCode";
	private static final String TENANT_ID = "/?tenantId=";
	private String accessToken;
	
	@Autowired
	UserService userService;
	
	private String tableName = "automatedTestCollection3";
	private int tenantId = 101;
	
	List<SchemaField> attributes = new ArrayList<>(Arrays.asList(
			new SchemaField("testField6", "string", true, true, false, true, true, false)));
	String expectedCapacityPlans = "{\"plans\":[{\"shards\":1,\"replicas\":1,\"name\":\"Basic\",\"sku\":\"B\"},{\"shards\":2,"
			+ "\"replicas\":2,\"name\":\"Standard\",\"sku\":\"S1\"},{\"shards\":3,\"replicas\":2,"
			+ "\"name\":\"Standard\",\"sku\":\"S2\"},{\"shards\":7,\"replicas\":3,\"name\":\"Standard\","
			+ "\"sku\":\"S3\"},{\"shards\":5,\"replicas\":3,\"name\":\"Premium\",\"sku\":\"P\"}],"
			+ "\"message\":\"Successfully retrieved all Capacity Plans\",\"statusCode\":200}";
	
	@BeforeAll
	void setToken() {
	    accessToken = userService.getToken(new User(username, password)).getToken();
	    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
	}
	
	  int createTable(String tableName) {
		CreateTable createTableDTO = new CreateTable(tableName, "B", attributes);	
		HttpEntity<CreateTable> entity = new HttpEntity<>(createTableDTO, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
		createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/" + TENANT_ID + tenantId),
				entity, String.class);
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	  
	int getTableInfo(String tableName) {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/" + tableName + TENANT_ID + tenantId),
				HttpMethod.GET, entity, String.class);
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	String getCapacityPlans() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers); 
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/capacity-plans"),
				HttpMethod.GET, entity, String.class);
		return  new JSONObject(response.getBody()).toString();
	}
	
	int getTablesForTenantID() {
	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + TENANT_ID + tenantId),
				HttpMethod.GET, entity, String.class);
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	int getAllTables() {
	    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/all-tables" + "?pageNumber=1&pageSize=5"),
				HttpMethod.GET, entity, String.class);
       	return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	int updateTable() {
		ManageTable schemaDTO = new ManageTable(tableName, attributes);
		HttpEntity<ManageTable> entity = new HttpEntity<>(schemaDTO, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/" + tableName + TENANT_ID + tenantId),
				HttpMethod.PUT, entity, String.class);
	    return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	int tableDelete(String tableName) {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/" + tableName + "/?tenantId=" + tenantId),
				HttpMethod.DELETE, entity, String.class);	
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	int getAllTablesUnderDeletion() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/deletion/all-tables" + "?pageNumber=1&pageSize=5"),
				HttpMethod.GET, entity, String.class);
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}
	
	int restoreTable() {
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				createURLWithPort(apiEndpoint+ MANAGE_TABLE + "/restore/" + "/" + tableName + TENANT_ID + tenantId),
				HttpMethod.PUT, entity, String.class);
		return new JSONObject(response.getBody()).getInt(STATUS_CODE);
	}

	
	@Order(1)
	@Test
	void testCreateTable()
	{	
		assertEquals(200, createTable(tableName));	
	}
	
	@Order(2)
	@Test
	void testGetTableInfo()
	{
		assertEquals(200, getTableInfo(tableName));	
	}
	
	@Order(3)
	@Test
       void testGetCapacityPlans()
	{
	   assertEquals(expectedCapacityPlans, getCapacityPlans());
		
	}
	
	@Order(4)
	@Test
	void getTableswithTenantId()
	{
		assertEquals(200, getTablesForTenantID());
	}
	
	@Order(5)
	@Test
	void testGetAllTables()
	{
		assertEquals(200, getAllTables());
		
	}
	
	@Order(6)   
	@Test
	void updatetabletest()
	{
		assertEquals(200, updateTable());
		
	}
	
	@Order(7)
	@Test
	void deleteTable()
	{	
		assertEquals(200, tableDelete(tableName));	
	}
	
	@Order(8)
	@Test 
	void testGetAllTablesUnderDeletion()
	{	
		assertEquals(200, getAllTablesUnderDeletion());
       
	}
	
	@Order(9)
	@Test
	void tableInvalidName()
	{	
		assertEquals(HttpStatusCode.INVALID_TABLE_NAME.getCode(),  createTable(tableName+"_90"));
		
	}
	
	@Order(10)
	@Test
	void creatingTableUnderDeletion()
	{
		assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), createTable(tableName));
	}

	@Order(11)
	@Test
	void getInfoDeleteTable()
	{	
		assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), getTableInfo(tableName));
	}
	
	@Order(12)
	@Test
	void getInfoTableNotFound()
	{
		assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), getTableInfo(tableName+"123"));
	}
	
	@Order(13)
	@Test
	void deleteTableNotFound()
	{
		assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), tableDelete(tableName + "90"));
	}
	
	@Order(14)
	@Test
	void testTableUnderDeletion()
	{
		assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), tableDelete(tableName));
	}
	
	@Order(15)
	@Test
	void getInfoTableUnderDeletion()
	{
		assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),  getTableInfo(tableName));
	}
	
	 @Order(16)
	@Test
	void testRestoreDeleteTable() 
	{
	   	assertEquals(200, restoreTable());		
	}
	
	@Order(17)
	@Test
	void restoreTestTableNotUnderDeletion()
	{
		assertEquals(HttpStatusCode.TABLE_NOT_UNDER_DELETION.getCode(), restoreTable());
	}
	
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
	
	@AfterAll
	void after()
	{
		manageTableServicePort.deleteTable(tableName+"_"+tenantId);
	}
	
}
