package com.searchservice.app.rest.inputdocument;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.service.InputDocumentService;
import com.searchservice.app.domain.service.security.KeycloakUserPermission;
import com.searchservice.app.domain.utils.security.SecurityUtil;

@IntegrationTest
@AutoConfigureMockMvc	//(addFilters = false)
class InputDocumentResourceTest {

	@Value("${custom-mock.jwt-token}")
	private String accessToken;
	
	// String apiEndpoint = "/api/v1";
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	String name;
	String message="";
	int tenantId = 101;
	String tableName = "book";
	String expectedGetResponse = "{\r\n" + "  \"statusCode\": 200,\r\n" + "  \"name\": \"book\",\r\n"
			+ "  \"message\": \"Successfully Added!\"\r\n" + "}";

	String expectedCreateResponse400 = "{\r\n" + "  \"statusCode\": 400,\r\n" + "  \"name\": \"booksdfsd\",\r\n"
			+ "  \"message\": \"Unable to get the Schema. Please check the collection name again!\"\r\n" + "}";

	String inputString = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";

    @Autowired
    MockMvc restAMockMvc;

	@MockBean
	InputDocumentService inputDocumentService;
		
	@MockBean
	ManageTableServicePort manageTableServicePort;

	@MockBean(name = "keycloakAuthService")
	private KeycloakUserPermission keycloakUserPermission;
	
	public void setMockitoSucccessResponseForService() {
		ThrottlerResponse responseDTO = new ThrottlerResponse();
		responseDTO.setStatusCode(200);
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		Mockito.when(inputDocumentService.addDocuments(Mockito.anyBoolean(),Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(true);
	}

	public void setMockitoBadResponseForService() {
		ThrottlerResponse responseDTO = new ThrottlerResponse();
		responseDTO.setStatusCode(400);
		Mockito.when(inputDocumentService.addDocuments(Mockito.anyBoolean(),Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(true);
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		ResponseEntity<ThrottlerResponse> responseEntity = new ResponseEntity<ThrottlerResponse>(
				HttpStatus.BAD_REQUEST);
		Mockito.when(inputDocumentService.documentInjectWithInvalidTableName(tenantId, tableName)).thenReturn(responseEntity);
	}
	
	public void mockPreAuthorizedService() {
		when(keycloakUserPermission.isViewPermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isCreatePermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isEditPermissionEnabled()).thenReturn(true);
		when(keycloakUserPermission.isDeletePermissionEnabled()).thenReturn(true);
	}
	
	@Test
	void testInputDocumentNRTAPI() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
					.thenReturn(true);
			
			mockPreAuthorizedService();
			setMockitoSucccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(inputString))
					.andExpect(status().isOk());
			
			TimeUnit.SECONDS.sleep(10);
			
			setMockitoBadResponseForService();
			Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(inputString))
					.andExpect(status().isBadRequest());
			
			setMockitoBadResponseForService();
			Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(false);
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(inputString))
					.andExpect(status().isBadRequest());
		}
	}

	@Test
	void testInputDocumentBatchAPI() throws Exception {
		try (MockedStatic<SecurityUtil> mockedUtility = Mockito.mockStatic(SecurityUtil.class)) {
			mockedUtility.when(() -> SecurityUtil.validate(Mockito.anyString(), Mockito.anyString()))
					.thenReturn(true);
			
			mockPreAuthorizedService();
			TimeUnit.SECONDS.sleep(10);
			setMockitoSucccessResponseForService();
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(inputString)).andExpect(status().isOk());
			
			setMockitoBadResponseForService();
			Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(inputString))
					.andExpect(status().isBadRequest());
			
			TimeUnit.SECONDS.sleep(10);
			setMockitoBadResponseForService();
			Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(false);
			restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.content(inputString))
					.andExpect(status().isBadRequest());
		}
	}	
	
}