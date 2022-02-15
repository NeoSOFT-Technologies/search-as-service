package com.search.app.inputdocument;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.service.InputDocumentService;

@IntegrationTest
@AutoConfigureMockMvc
class InputDocumentResourceTest {

	// String apiEndpoint = "/api/v1";
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	int statusCode;
	String name;
	String message;
	int clientid;
	String tableName = "book";
	String expectedGetResponse = "{\r\n" + "  \"statusCode\": 200,\r\n" + "  \"name\": \"book\",\r\n"
			+ "  \"message\": \"Successfully Added!\"\r\n" + "}";

	String expectedCreateResponse400 = "{\r\n" + "  \"statusCode\": 400,\r\n" + "  \"name\": \"booksdfsd\",\r\n"
			+ "  \"message\": \"Unable to get the Schema. Please check the collection name again!\"\r\n" + "}";

	String inputString = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";

	@Autowired
	private MockMvc restAMockMvc;

	@MockBean
	InputDocumentService inputDocumentService;

	public void setMockitoSucccessResponseForService() {
		ThrottlerResponse responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		Mockito.when(inputDocumentService.addDocument(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.addDocuments(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
	}

	public void setMockitoBadResponseForService() {
		ThrottlerResponse responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		Mockito.when(inputDocumentService.addDocument(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.addDocuments(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
	}

	@Test
	void testinputdocs() throws Exception {
		ThrottlerResponse responseDTO = new ThrottlerResponse(statusCode, message);
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + clientid + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
				.andExpect(status().isOk());
	}

	@Test
	void testinputdoc() throws Exception {
		ThrottlerResponse responseDTO = new ThrottlerResponse(statusCode, message);
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + clientid + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
				.andExpect(status().isOk());
	}
}