package com.searchservice.app.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil.UploadDocumentSearchUtilRespnse;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
class InputDocumentServiceTest {

	int tenantId = 1;
	String tableName = "book";
	String tableNames = "";
	String payload = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";
	int statusCode = 0;
	String name;
	String message = "";

	@MockBean
	private ManageTableServicePort manageTableServiceport;

	@InjectMocks
	private InputDocumentService inputDocumentService;

	@MockBean
	private ThrottlerResponse responseDTO;

	@MockBean
	UploadDocumentSearchUtilRespnse response;

	@MockBean
	UploadDocumentUtil uploadDocumentUtil;

	@BeforeEach
	void setUp() throws Exception {
		responseDTO = new ThrottlerResponse();
		responseDTO.setMessage("Document Uplode");
		responseDTO.setStatusCode(200);
		response.setDocumentUploaded(true);	

	}

	public void setMockitoSucccessResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		responseDTO.setMessage(message);
		response.setDocumentUploaded(true);
		Mockito.doReturn(response).when(uploadDocumentUtil).softcommit();
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
		//Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
	
	}

	public void setMockitoBadResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		Mockito.when(manageTableServiceport.isTableExists(Mockito.anyString())).thenReturn(false);

	}

	@Test
	void testAddDocument() {
		
		setMockitoSucccessResponseForService();	
		ThrottlerResponse response1 = inputDocumentService.addDocument(tableName, payload);
		assertEquals(200, response1.getStatusCode());

	}

	@Test
	void testAddDocuments() {
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
		response.setDocumentUploaded(true);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(tableName, payload);
		assertEquals(400, response.getStatusCode());

	}

	@Test
	void testBadDocument() {
		setMockitoBadResponseForService();

		try {
			inputDocumentService.addDocument(tableName, payload);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void testBadDocuments() {
		setMockitoBadResponseForService();

		try {
			inputDocumentService.addDocument(tableName, payload);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}

	}
	

	@Test
	void testBadAddDocuments() {
		setMockitoBadResponseForService();

		try {
			inputDocumentService.addDocuments(tableName, payload);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void isValidJsonArrayFailure() {
		boolean b = inputDocumentService.isValidJsonArray(message);
		assertFalse(b);

	}

	@Test
	void isValidJsonArraySuccess() {
		boolean b = inputDocumentService.isValidJsonArray(payload);
		assertTrue(b);

	}
}
