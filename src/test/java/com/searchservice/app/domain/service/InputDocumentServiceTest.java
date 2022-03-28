package com.searchservice.app.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.InputDocumentServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil.UploadDocumentSearchUtilRespnse;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class InputDocumentServiceTest {

	int tenantId = 1;
	String tableName = "book";
	String tableNames = "";
	String payload = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";
	int statusCode = 0;
	String name;
	String message = "";

	@MockBean
	private ManageTableServicePort manageTableServicePort;

	@InjectMocks
	private InputDocumentService inputDocumentService;

	@MockBean
	private InputDocumentServicePort inputDocumentServiceport;

	@MockBean
	private LoggersDTO loggersDTO;

	private ThrottlerResponse responseDTO;

	@MockBean
	UploadDocumentSearchUtilRespnse response;

	@MockBean
	private UploadDocumentUtil uploadDocumentUtil;

	@BeforeEach
	void setUp() throws Exception {
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");

		responseDTO = new ThrottlerResponse();
		responseDTO.setMessage("Document Uplode");
		responseDTO.setStatusCode(200);
		response.setDocumentUploaded(true);
	
	}

	public void setMockitoSucccessResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		responseDTO.setMessage(message);

	//	inputDocumentServiceport.addDocument(name, payload, loggersDTO);
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);

		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
		Mockito.when(inputDocumentServiceport.addDocument(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
		.thenReturn(responseDTO);
	}

	public void setMockitoBadResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(false);

	}

	@Test
	void testAddDocument() {
	//	response.setDocumentUploaded(true);
		

		setMockitoSucccessResponseForService();

		ThrottlerResponse response = inputDocumentService.addDocument(tableName, payload, loggersDTO);

		assertEquals(200, response.getStatusCode());

	}

	@Test
	void testAddDocuments() {
		Mockito.when(inputDocumentServiceport.addDocuments(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn(responseDTO);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(tableName, payload, loggersDTO);
		assertEquals(200, responseDTO.getStatusCode());

	}

	@Test
	void testAddDocumentsss() {
		setMockitoBadResponseForService();
		Mockito.when(inputDocumentServiceport.addDocuments(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn(responseDTO);
		try {
			ThrottlerResponse response = inputDocumentService.addDocuments(tableName, payload, loggersDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void testAddDocumentsssp() {
		setMockitoBadResponseForService();
		Mockito.when(inputDocumentServiceport.addDocument(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
				.thenReturn(responseDTO);
		try {
			ThrottlerResponse response = inputDocumentService.addDocument(tableName, payload, loggersDTO);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

	@Test
	void isValidJsonArray() {

		try {
			boolean b = inputDocumentService.isValidJsonArray(message);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}

}