package com.search.app.inputdocument;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.ThrottlerServicePort;
import com.searchservice.app.domain.service.InputDocumentService;
import com.searchservice.app.domain.utils.HttpStatusCode;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class InputDocumentResourceTest {

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
	
	@Test
	void testInputDocumentNRTAPI() throws Exception {
		
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(inputString))
				.andExpect(status().isOk());
		
		TimeUnit.SECONDS.sleep(10);
		
		setMockitoBadResponseForService();
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(inputString))
				.andExpect(status().isBadRequest());
		
		setMockitoBadResponseForService();
		Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(false);
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(inputString))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testInputDocumentBatchAPI() throws Exception {
		TimeUnit.SECONDS.sleep(10);
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_JSON)
				.content(inputString)).andExpect(status().isOk());
		
		setMockitoBadResponseForService();
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(inputString))
				.andExpect(status().isBadRequest());
		
		TimeUnit.SECONDS.sleep(10);
		setMockitoBadResponseForService();
		Mockito.when(inputDocumentService.isValidJsonArray(Mockito.any())).thenReturn(false);
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + "/" + tableName+ "/?tenantId="+tenantId )
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.content(inputString))
				.andExpect(status().isBadRequest());	
	}	
	
}