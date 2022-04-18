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
		try {
			inputDocumentService.addDocuments(true, tableName, payload);
		}catch(BadRequestOccurredException e) {
			assertEquals(400,e.getExceptionCode());
		}
	}
	
	@Test
	void testAddDocumentsWithoutNrtTableNotExist() {
		tableNotExist();
		try {
			inputDocumentService.addDocuments(false, tableName, payload);
		}catch(BadRequestOccurredException e) {
			assertEquals(400,e.getExceptionCode());
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
	void testAddDocumentsWithoutNrt() {
		Mockito.when(manageTableServiceport.isTableExists(tableName)).thenReturn(true);
		response.setDocumentUploaded(true);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(false,tableName, payload);
		assertEquals(200, response.getStatusCode());

	}
	
	@Test
	void testBadAddDocumentsForNRT() {
		setMockitoBadResponseForService();

		try {
			inputDocumentService.addDocuments(true,tableName, payload);
		} catch (BadRequestOccurredException e) {
			assertEquals(400, e.getExceptionCode());
		}
	}

	@Test
	void testBadAddDocumentsWithoutNRT() {
		try {
			inputDocumentService.addDocuments(false,tableName, payload);
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