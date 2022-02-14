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
import com.searchservice.app.domain.dto.throttler.ThrottlerResponseDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.service.InputDocumentService;

@IntegrationTest
@AutoConfigureMockMvc
class InputDocumentResourceInvalidTest {
	
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;
	int statusCode;
	String name;
	String message;
	int clientid;
	String invalidTableName = "book"+"1234";
	
	String expectedCreateResponse400 = "{\r\n" + "  \"statusCode\": 400,\r\n" + "  \"name\": \"booksdfsd\",\r\n"
			+ "  \"message\": \"Unable to get the Schema. Please check the collection name again!\"\r\n" + "}";

	String inputString = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";

	@Autowired
	private MockMvc restAMockMvc;

	@MockBean
	InputDocumentService inputDocumentService;
	
	@MockBean
	ManageTableServicePort manageTableServicePort;
	
	public void setMockitoBadResponseForService() {
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO(statusCode, message);
		responseDTO.setStatusCode(400);
		Mockito.when(inputDocumentService.addDocument(Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.addDocuments(Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);
	}

	@Test
	void testinputdocsInvalidCase() throws Exception {
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO(statusCode, message);
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest-nrt/" + clientid + "/" + invalidTableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
				.andExpect(status().isBadRequest());	
	}

	@Test
	void testinputdocInvalidCase() throws Exception {
		ThrottlerResponseDTO responseDTO = new ThrottlerResponseDTO(statusCode, message);
		setMockitoBadResponseForService();
		restAMockMvc.perform(MockMvcRequestBuilders.post(apiEndpoint + "/ingest/" + clientid + "/" + invalidTableName+"090")
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
				.andExpect(status().isBadRequest());
	}
}
