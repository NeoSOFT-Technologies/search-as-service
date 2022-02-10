package com.search.app.inputdocument;


import com.searchservice.app.IntegrationTest;
import com.searchservice.app.TestUtil;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.document.DocumentResponseDTO;
import com.searchservice.app.domain.service.InputDocumentService;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.infrastructure.enums.SchemaFieldType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class InputDocumentResourceTest {

	String solrendpoint = "/api/v1";
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
		ResponseDTO responseDTO = new ResponseDTO(statusCode, name, message);
		responseDTO.setResponseStatusCode(200);
		Mockito.when(inputDocumentService.addDocument( Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.addDocuments(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);

	}

	public void setMockitoBadResponseForService() {
		ResponseDTO responseDTO = new ResponseDTO(statusCode, name, message);
		responseDTO.setResponseStatusCode(400);
		Mockito.when(inputDocumentService.addDocument(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(inputDocumentService.addDocuments(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
	}

	@Test
	void testinputdocs() throws Exception {	
		ResponseDTO responseDTO = new ResponseDTO(statusCode, name, message);	
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders 
				.post(solrendpoint + "/ingest-nrt/"+ clientid + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
		.andExpect(status().isOk());
		 

	}
	
	@Test
	void testinputdoc() throws Exception {	
		ResponseDTO responseDTO = new ResponseDTO(statusCode, name, message);	
		setMockitoSucccessResponseForService();
		restAMockMvc.perform(
				MockMvcRequestBuilders
				.post(solrendpoint + "/ingest/"+ clientid + "/" + tableName)
				.contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(responseDTO)))
		.andExpect(status().isOk());
		 

	}
}