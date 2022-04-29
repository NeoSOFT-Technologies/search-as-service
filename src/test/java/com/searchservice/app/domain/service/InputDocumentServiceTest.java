package com.searchservice.app.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil.UploadDocumentSearchUtilRespnse;
import com.searchservice.app.rest.errors.CustomException;
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
		response = new UploadDocumentSearchUtilRespnse(true, "Testing");

	}

	public void setMockitoSucccessResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		responseDTO.setMessage(message);
		response = new UploadDocumentSearchUtilRespnse(true, "Testing");
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
	
	}

	public void setMockitoBadResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		response = new UploadDocumentSearchUtilRespnse(false, "Testing");
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
	}

	
	public void tableExist() {
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
	}
	
	public void tableNotExist() {
		Mockito.when(manageTableServiceport.isTableExists(Mockito.anyString())).thenReturn(false);
	}
	
	@Test
	void testAddDocumentsNrtTableNotExist() {
		tableNotExist();
		setMockitoBadResponseForService();
		try {
			inputDocumentService.addDocuments(true, tableName, payload);
		}catch(CustomException e) {
			assertEquals(108,e.getExceptionCode());
		}
	}
	
	@Test
	void testAddDocumentsBatchTableNotExist() {
		tableNotExist();
		setMockitoBadResponseForService();
		try {
			inputDocumentService.addDocuments(false, tableName, payload);
		}catch(CustomException e) {
			assertEquals(108,e.getExceptionCode());
		}
	}


	@Test
	void testAddDocumentsNrt() {
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
		response.setDocumentUploaded(true);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(true,tableName, payload);
		assertEquals(200, response.getStatusCode());
	}
	
	@Test

	void testAddDocumentsBatch() {
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
		response.setDocumentUploaded(true);

		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(false,tableName, payload);
		assertEquals(200, response.getStatusCode());

	}
	
	@Test
	void testBadAddDocumentsForNRT() {
		setMockitoBadResponseForService();
		tableExist();
		ThrottlerResponse response =inputDocumentService.addDocuments(true,tableName, payload);
		assertEquals(400, response.getStatusCode());
	}

	@Test
	void testBadAddDocumentsWithoutNRT() {
		setMockitoBadResponseForService();
		tableExist();
		ThrottlerResponse response =inputDocumentService.addDocuments(false,tableName, payload);
		assertEquals(400, response.getStatusCode());
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

	@Test
	void testDocumentInjectWithInvalidTableName() {
		ResponseEntity<ThrottlerResponse> responseEntity = inputDocumentService.documentInjectWithInvalidTableName(tenantId, tableName);
		assertEquals(400, responseEntity.getStatusCodeValue());
	}
	
	@Test
	void testPerformDocumentInjection() {
		Mockito.when(manageTableServiceport.isTableExists(Mockito.anyString())).thenReturn(true);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityBatch = inputDocumentService.performDocumentInjection(false, tableName, payload, responseDTO);
		assertEquals(200, responseEntityBatch.getStatusCodeValue());
		
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityNRT = inputDocumentService.performDocumentInjection(true, tableName, payload, responseDTO);
		assertEquals(200, responseEntityNRT.getStatusCodeValue());
	}
	
}