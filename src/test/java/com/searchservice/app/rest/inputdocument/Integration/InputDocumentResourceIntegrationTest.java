package com.searchservice.app.rest.inputdocument.Integration;

import static org.junit.jupiter.api.Assertions.*;
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
import org.junit.runner.RunWith;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import com.searchservice.app.SearchServiceApplication;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.user.User;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.UserServicePort;
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
class InputDocumentResourceIntegrationTest {
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	
	@LocalServerPort
	private int port;
	
	@Autowired
	ManageTableServicePort manageTableServicePort;
	
	@Autowired
	UserServicePort userServicePort;
	
	@Value("${username}")
	private String username;
	
	@Value("${password}")
	private String password;

	TestRestTemplate restTemplate = new TestRestTemplate();
	
	HttpHeaders headers = new HttpHeaders();
	static private String accessToken;
	private String successPayloadString = "[{\"id\":101,\"name\":\"JamieCampbell\",\"title\":\"JamieCampbell\",\"productName\":\"Amazingtitle-JamieCampbell\",\"category\":2,\"price\":\"56267\",\"vendorId\":\"13\",\"isAvailable\":true}]";
	private String invalidPayloadString = "[{\"id\":101,\"name\":\"JamieCampbell\"\"title\":\"JamieCampbell\"\"productName\":\"Amazingtitle-JamieCampbell\",\"category\":2,\"price\":\"56267\",\"vendorId\":\"13\",\"isAvailable\":true}]";
	private String largeSizePayloadString = "[{\"id\":1,\"name\":\"ChristopherWhitaker\",\"title\":\"ChristopherWhitaker\",\"productName\":\"Amazingtitle-ChristopherWhitaker\",\"category\":4,\"price\":\"47363\",\"vendorId\":\"12\",\"isAvailable\":false},{\"id\":2,\"name\":\"JamieCampbell\",\"title\":\"JamieCampbell\",\"productName\":\"Amazingtitle-JamieCampbell\",\"category\":2,\"price\":\"56267\",\"vendorId\":\"13\",\"isAvailable\":true},{\"id\":3,\"name\":\"KimberlyHawkinsDDS\",\"title\":\"KimberlyHawkinsDDS\",\"productName\":\"Amazingtitle-KimberlyHawkinsDDS\",\"category\":2,\"price\":\"44553\",\"vendorId\":\"0\",\"isAvailable\":true},{\"id\":4,\"name\":\"CarlaHerman\",\"title\":\"CarlaHerman\",\"productName\":\"Amazingtitle-CarlaHerman\",\"category\":3,\"price\":\"2671\",\"vendorId\":\"9\",\"isAvailable\":true},{\"id\":5,\"name\":\"RyanChavez\",\"title\":\"RyanChavez\",\"productName\":\"Amazingtitle-RyanChavez\",\"category\":5,\"price\":\"41435\",\"vendorId\":\"4\",\"isAvailable\":true},{\"id\":11,\"name\":\"KevinTorres\",\"title\":\"KevinTorres\",\"productName\":\"Amazingtitle-KevinTorres\",\"category\":4,\"price\":\"7124\",\"vendorId\":\"14\",\"isAvailable\":true},{\"id\":12,\"name\":\"KevinPhillips\",\"title\":\"KevinPhillips\",\"productName\":\"Amazingtitle-KevinPhillips\",\"category\":3,\"price\":\"35776\",\"vendorId\":\"13\",\"isAvailable\":false},{\"id\":13,\"name\":\"CrystalMoody\",\"title\":\"CrystalMoody\",\"productName\":\"Amazingtitle-CrystalMoody\",\"category\":5,\"price\":\"51987\",\"vendorId\":\"10\",\"isAvailable\":false},{\"id\":14,\"name\":\"ArthurGood\",\"title\":\"ArthurGood\",\"productName\":\"Amazingtitle-ArthurGood\",\"category\":5,\"price\":\"13905\",\"vendorId\":\"15\",\"isAvailable\":true},{\"id\":15,\"name\":\"JonathonKeith\",\"title\":\"JonathonKeith\",\"productName\":\"Amazingtitle-JonathonKeith\",\"category\":4,\"price\":\"90024\",\"vendorId\":\"7\",\"isAvailable\":false},{\"id\":21,\"name\":\"MargaretGarcia\",\"title\":\"MargaretGarcia\",\"productName\":\"Amazingtitle-MargaretGarcia\",\"category\":5,\"price\":\"80471\",\"vendorId\":\"12\",\"isAvailable\":true},{\"id\":22,\"name\":\"JoshuaRodriguez\",\"title\":\"JoshuaRodriguez\",\"productName\":\"Amazingtitle-JoshuaRodriguez\",\"category\":1,\"price\":\"3619\",\"vendorId\":\"8\",\"isAvailable\":true},{\"id\":23,\"name\":\"RachelMolina\",\"title\":\"RachelMolina\",\"productName\":\"Amazingtitle-RachelMolina\",\"category\":2,\"price\":\"83005\",\"vendorId\":\"6\",\"isAvailable\":true},{\"id\":24,\"name\":\"ScottRichardson\",\"title\":\"ScottRichardson\",\"productName\":\"Amazingtitle-ScottRichardson\",\"category\":1,\"price\":\"99315\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":25,\"name\":\"GregoryRichardson\",\"title\":\"GregoryRichardson\",\"productName\":\"Amazingtitle-GregoryRichardson\",\"category\":5,\"price\":\"63819\",\"vendorId\":\"13\",\"isAvailable\":true},{\"id\":31,\"name\":\"LynnTaylor\",\"title\":\"LynnTaylor\",\"productName\":\"Amazingtitle-LynnTaylor\",\"category\":4,\"price\":\"18213\",\"vendorId\":\"3\",\"isAvailable\":false},{\"id\":32,\"name\":\"LoriMeyers\",\"title\":\"LoriMeyers\",\"productName\":\"Amazingtitle-LoriMeyers\",\"category\":2,\"price\":\"60770\",\"vendorId\":\"13\",\"isAvailable\":false},{\"id\":33,\"name\":\"WilliamLawrence\",\"title\":\"WilliamLawrence\",\"productName\":\"Amazingtitle-WilliamLawrence\",\"category\":1,\"price\":\"75644\",\"vendorId\":\"12\",\"isAvailable\":true},{\"id\":34,\"name\":\"ChristopherWilliams\",\"title\":\"ChristopherWilliams\",\"productName\":\"Amazingtitle-ChristopherWilliams\",\"category\":4,\"price\":\"10718\",\"vendorId\":\"4\",\"isAvailable\":false},{\"id\":35,\"name\":\"AmyWatson\",\"title\":\"AmyWatson\",\"productName\":\"Amazingtitle-AmyWatson\",\"category\":3,\"price\":\"68942\",\"vendorId\":\"4\",\"isAvailable\":false},{\"id\":41,\"name\":\"WilliamThompson\",\"title\":\"WilliamThompson\",\"productName\":\"Amazingtitle-WilliamThompson\",\"category\":4,\"price\":\"97947\",\"vendorId\":\"9\",\"isAvailable\":true},{\"id\":42,\"name\":\"AustinLawson\",\"title\":\"AustinLawson\",\"productName\":\"Amazingtitle-AustinLawson\",\"category\":2,\"price\":\"74946\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":43,\"name\":\"KarenJohnson\",\"title\":\"KarenJohnson\",\"productName\":\"Amazingtitle-KarenJohnson\",\"category\":4,\"price\":\"91150\",\"vendorId\":\"15\",\"isAvailable\":false},{\"id\":44,\"name\":\"MiguelClark\",\"title\":\"MiguelClark\",\"productName\":\"Amazingtitle-MiguelClark\",\"category\":3,\"price\":\"19305\",\"vendorId\":\"9\",\"isAvailable\":true},{\"id\":45,\"name\":\"AmandaGlass\",\"title\":\"AmandaGlass\",\"productName\":\"Amazingtitle-AmandaGlass\",\"category\":5,\"price\":\"13422\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":51,\"name\":\"EricChristensen\",\"title\":\"EricChristensen\",\"productName\":\"Amazingtitle-EricChristensen\",\"category\":4,\"price\":\"19326\",\"vendorId\":\"8\",\"isAvailable\":true},{\"id\":52,\"name\":\"AustinWilliams\",\"title\":\"AustinWilliams\",\"productName\":\"Amazingtitle-AustinWilliams\",\"category\":5,\"price\":\"65794\",\"vendorId\":\"8\",\"isAvailable\":true},{\"id\":53,\"name\":\"MichaelLandry\",\"title\":\"MichaelLandry\",\"productName\":\"Amazingtitle-MichaelLandry\",\"category\":3,\"price\":\"13207\",\"vendorId\":\"11\",\"isAvailable\":false},{\"id\":54,\"name\":\"JessicaCruz\",\"title\":\"JessicaCruz\",\"productName\":\"Amazingtitle-JessicaCruz\",\"category\":1,\"price\":\"45163\",\"vendorId\":\"3\",\"isAvailable\":true},{\"id\":55,\"name\":\"JohnWaters\",\"title\":\"JohnWaters\",\"productName\":\"Amazingtitle-JohnWaters\",\"category\":3,\"price\":\"99753\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":61,\"name\":\"RobertRamos\",\"title\":\"RobertRamos\",\"productName\":\"Amazingtitle-RobertRamos\",\"category\":5,\"price\":\"77214\",\"vendorId\":\"6\",\"isAvailable\":false},{\"id\":62,\"name\":\"SamanthaLiu\",\"title\":\"SamanthaLiu\",\"productName\":\"Amazingtitle-SamanthaLiu\",\"category\":3,\"price\":\"71159\",\"vendorId\":\"7\",\"isAvailable\":false},{\"id\":63,\"name\":\"NathanFergusonII\",\"title\":\"NathanFergusonII\",\"productName\":\"Amazingtitle-NathanFergusonII\",\"category\":5,\"price\":\"59286\",\"vendorId\":\"8\",\"isAvailable\":false},{\"id\":64,\"name\":\"AnthonyRamos\",\"title\":\"AnthonyRamos\",\"productName\":\"Amazingtitle-AnthonyRamos\",\"category\":4,\"price\":\"45066\",\"vendorId\":\"4\",\"isAvailable\":false},{\"id\":65,\"name\":\"KatherineHoward\",\"title\":\"KatherineHoward\",\"productName\":\"Amazingtitle-KatherineHoward\",\"category\":5,\"price\":\"66169\",\"vendorId\":\"1\",\"isAvailable\":false},{\"id\":71,\"name\":\"DanielBurgess\",\"title\":\"DanielBurgess\",\"productName\":\"Amazingtitle-DanielBurgess\",\"category\":5,\"price\":\"22936\",\"vendorId\":\"13\",\"isAvailable\":false},{\"id\":72,\"name\":\"CurtisPerkins\",\"title\":\"CurtisPerkins\",\"productName\":\"Amazingtitle-CurtisPerkins\",\"category\":3,\"price\":\"38230\",\"vendorId\":\"0\",\"isAvailable\":false},{\"id\":73,\"name\":\"BarbaraMiller\",\"title\":\"BarbaraMiller\",\"productName\":\"Amazingtitle-BarbaraMiller\",\"category\":3,\"price\":\"43607\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":74,\"name\":\"MeganVincent\",\"title\":\"MeganVincent\",\"productName\":\"Amazingtitle-MeganVincent\",\"category\":2,\"price\":\"94922\",\"vendorId\":\"8\",\"isAvailable\":true},{\"id\":75,\"name\":\"ChristopherSmith\",\"title\":\"ChristopherSmith\",\"productName\":\"Amazingtitle-ChristopherSmith\",\"category\":2,\"price\":\"63475\",\"vendorId\":\"11\",\"isAvailable\":false},{\"id\":81,\"name\":\"LauraBrown\",\"title\":\"LauraBrown\",\"productName\":\"Amazingtitle-LauraBrown\",\"category\":1,\"price\":\"82735\",\"vendorId\":\"9\",\"isAvailable\":false},{\"id\":82,\"name\":\"EmilyMarshall\",\"title\":\"EmilyMarshall\",\"productName\":\"Amazingtitle-EmilyMarshall\",\"category\":3,\"price\":\"84296\",\"vendorId\":\"15\",\"isAvailable\":false},{\"id\":83,\"name\":\"MatthewHarvey\",\"title\":\"MatthewHarvey\",\"productName\":\"Amazingtitle-MatthewHarvey\",\"category\":1,\"price\":\"30893\",\"vendorId\":\"11\",\"isAvailable\":true},{\"id\":84,\"name\":\"AndreaStrickland\",\"title\":\"AndreaStrickland\",\"productName\":\"Amazingtitle-AndreaStrickland\",\"category\":5,\"price\":\"5400\",\"vendorId\":\"11\",\"isAvailable\":true},{\"id\":85,\"name\":\"KeithCantu\",\"title\":\"KeithCantu\",\"productName\":\"Amazingtitle-KeithCantu\",\"category\":5,\"price\":\"70739\",\"vendorId\":\"5\",\"isAvailable\":false},{\"id\":91,\"name\":\"MaryForbes\",\"title\":\"MaryForbes\",\"productName\":\"Amazingtitle-MaryForbes\",\"category\":4,\"price\":\"81602\",\"vendorId\":\"9\",\"isAvailable\":false},{\"id\":92,\"name\":\"NicholasHayden\",\"title\":\"NicholasHayden\",\"productName\":\"Amazingtitle-NicholasHayden\",\"category\":2,\"price\":\"18149\",\"vendorId\":\"11\",\"isAvailable\":true},{\"id\":93,\"name\":\"KatherineWright\",\"title\":\"KatherineWright\",\"productName\":\"Amazingtitle-KatherineWright\",\"category\":2,\"price\":\"97033\",\"vendorId\":\"12\",\"isAvailable\":false},{\"id\":94,\"name\":\"PeterStuart\",\"title\":\"PeterStuart\",\"productName\":\"Amazingtitle-PeterStuart\",\"category\":3,\"price\":\"56962\",\"vendorId\":\"4\",\"isAvailable\":true},{\"id\":95,\"name\":\"NatalieConner\",\"title\":\"NatalieConner\",\"productName\":\"Amazingtitle-NatalieConner\",\"category\":1,\"price\":\"29497\",\"vendorId\":\"9\",\"isAvailable\":true}]";
	
	private String tableName = "automatedTest2";
	private String invalidTableName = "automatedCollec";
	private String tenantId = "102";
	
	public List<SchemaField> attributes;
	
	@BeforeAll
	void setUpBeforeTest() {
		setToken();
		createAttributesList();
		createTable();
	}
	
	void setToken() {
		  accessToken = userServicePort.getToken(new User(username, password)).getToken();
		  headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
	}
	
	void createAttributesList() {
		attributes = new ArrayList<>(Arrays.asList(new SchemaField("category", "strings", false, true, true, true, false, false),
				new SchemaField("category", "strings", false, true, true, true, false, false),
				new SchemaField("isAvailable", "boolean", false, true, true, false, true, false),
				new SchemaField("name", "string", false, false, false, false, false, false),
				new SchemaField("price", "long", true, true, true, false, true, false),
				new SchemaField("productName", "string", false, true, true, false, false, true),
				new SchemaField("title", "string", false, true, true, false, false, true),
				new SchemaField("vendorId", "longs", false, true, true, true, false, false)));
	}
	
	void createTable() {
		CreateTable createTableDTO = new CreateTable();
		createTableDTO.setSku("B");
		createTableDTO.setTableName(tableName+"_"+tenantId);
		manageTableServicePort.createTable(createTableDTO);
	}
	
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}
	
	
	// Test Functions	
	int addDocumentsNRT() {
		HttpEntity<String> entity = new HttpEntity<>(successPayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest-nrt" + "/" + tableName+ "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");
	}
	
	int addDocumentsNRTWithInvalidJSONInput() {
		HttpEntity<String> entity = new HttpEntity<>(invalidPayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest-nrt" + "/" + tableName+ "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");
	}
	
	int addDocumentsNRTRequestSizeLimiting() {
		HttpEntity<String> entity = new HttpEntity<>(largeSizePayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest-nrt" + "/" + tableName+ "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");
	}
	
	int addDocumentsNRTTableNotFound() {
		HttpEntity<String> entity = new HttpEntity<>(successPayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest-nrt" + "/" + invalidTableName+ "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");
	}
	
	
	int addDocumentsBatchSuccess() {
		HttpEntity<String> entity = new HttpEntity<>(largeSizePayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest/" + tableName + "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");	
	}
	
	int addDocumentsBatchInvalidJsonInput() {
		HttpEntity<String> entity = new HttpEntity<>(invalidPayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest/" + tableName + "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");	
	}
	
	int addDocumentsBatchTableNotFound() {
		HttpEntity<String> entity = new HttpEntity<>(successPayloadString, headers);
		ResponseEntity<String> response = restTemplate.postForEntity(
				createURLWithPort(apiEndpoint + "/ingest/" + invalidTableName + "?tenantId="+tenantId )
				,entity, String.class);
	    return new JSONObject(response.getBody()).getInt("statusCode");	
	}
	
	// Test cases
	@Order(1)
	@Test 
	void testAddDocumentsNRTSuccessTest()
	{	
		assertEquals(200, addDocumentsNRT());
       
	}
	
	@Test 
	void testAddDocumentsNRTWithInvalidJSONInput()
	{	
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getCode(), addDocumentsNRTWithInvalidJSONInput());
       
	}
	
	@Test 
	void testAddDocumentsNRTRequestSizeLimiting()
	{	
		assertEquals(HttpStatusCode.NOT_ACCEPTABLE_ERROR.getCode(), addDocumentsNRTRequestSizeLimiting());
       
	}
	
	@Test 
	void testAddDocumentsNRTTableNotFound()
	{	
		assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), addDocumentsNRTTableNotFound());
       
	}
	
	@Test 
	void testAddDocumentsBatchSuccess()
	{	
		assertEquals(200, addDocumentsBatchSuccess());
       
	}
	
	@Test 
	void testAddDocumentsBatchInvalidJsonInput()
	{	
		assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getCode(), addDocumentsBatchInvalidJsonInput());
       
	}
	
	@Test 
	void testAddDocumentsBatchTableNotFound()
	{	
		assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), addDocumentsBatchTableNotFound());
       
	}
	
	@AfterAll
	void deleteTable() {
		manageTableServicePort.deleteTable(tableName+"_"+tenantId);
	}
	
}
