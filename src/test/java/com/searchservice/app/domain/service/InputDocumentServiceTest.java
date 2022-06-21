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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.throttler.ThrottlerResponse;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.UploadDocumentUtil;
import com.searchservice.app.domain.utils.UploadDocumentUtil.UploadDocumentSearchUtilRespnse;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class InputDocumentServiceTest {

	int tenantId = 1;
	String tableName = "book_101";
	String tableNames = "";
	String payload = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";
	int statusCode = 0;
	String name;
	String message = "";

	@MockBean
	private ManageTableServicePort manageTableServiceport;
	
	@MockBean
	private TableDeleteServicePort tableDeleteServicePort;

	@InjectMocks
	private InputDocumentService inputDocumentService;

	@MockBean
	private ThrottlerResponse responseDTO;

	@MockBean
	UploadDocumentSearchUtilRespnse response;

	@MockBean
	UploadDocumentUtil uploadDocumentUtil;
	
	@MockBean
	SearchJAdapter searchJAdapter;

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
		Mockito.when(searchJAdapter.checkIfSearchServerDown()).thenReturn(false);
	}

	public void setMockitoTableExists() {
		Mockito.when(manageTableServiceport.isTableExists(Mockito.anyString())).thenReturn(true);
	}
	
	public void setMockitoTableNotExists() {
		Mockito.when(manageTableServiceport.isTableExists(Mockito.anyString())).thenReturn(false);
	}
	public void setMockitoBadResponseForService() {
		responseDTO = new ThrottlerResponse(statusCode, message);
		responseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		response = new UploadDocumentSearchUtilRespnse(false, "Testing");
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
	}

	
	public void tableUnderDeletion() {
		Mockito.when(tableDeleteServicePort.isTableUnderDeletion(tableName)).thenReturn(true);
	}
	
	public void tableNotUnderDeletion() {
		Mockito.when(tableDeleteServicePort.isTableUnderDeletion(tableName)).thenReturn(false);
	}
		

	@Test
	void testAddDocumentsNrt() {
		tableNotUnderDeletion();
		setMockitoTableExists();
		response.setDocumentUploaded(true);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(true,tableName, payload);
		assertEquals(200, response.getStatusCode());
	}
	
	@Test
	void testAddDocumentsBatch() {
		setMockitoTableExists();
		tableNotUnderDeletion();
		response.setDocumentUploaded(true);
		setMockitoSucccessResponseForService();
		ThrottlerResponse response = inputDocumentService.addDocuments(false,tableName, payload);
		assertEquals(200, response.getStatusCode());

	}
	
	@Test
	void testAddDocumentsBatchInvalidJson() {
		setMockitoTableExists();
		tableNotUnderDeletion();
		setMockitoTableExists();
		setMockitoSucccessResponseForService();
		try {
			   inputDocumentService.addDocuments(false,tableName, "[{}");
			}
			catch(CustomException e) {
				assertEquals(HttpStatusCode.INVALID_JSON_INPUT.getCode(), e.getExceptionCode());
			}

	}
	
	@Test
	void testBadAddDocumentsForNRT() {
		setMockitoTableExists();
		setMockitoBadResponseForService();
		tableNotUnderDeletion();
		ThrottlerResponse response =inputDocumentService.addDocuments(true,tableName, payload);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), response.getStatusCode());
	}
	
	@Test
	void testTableNotExistNRT() {
		setMockitoSucccessResponseForService();
		tableNotUnderDeletion();
		setMockitoTableNotExists();
		try {
		   inputDocumentService.addDocuments(true,tableName, payload);
		}
		catch(CustomException e) {
			assertEquals(HttpStatusCode.TABLE_NOT_FOUND.getCode(), e.getExceptionCode());
		}
	}

	@Test
	void testBadAddDocumentsWithoutNRT() {
		setMockitoTableExists();
		setMockitoBadResponseForService();
		tableNotUnderDeletion();
		ThrottlerResponse response =inputDocumentService.addDocuments(false,tableName, payload);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), response.getStatusCode());
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
	void testAddDocumentsNRTTableUnderDeletion() {
		tableUnderDeletion();
		try {
			inputDocumentService.addDocuments(false,tableName, payload);
		}catch(CustomException e) {
			assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), e.getExceptionCode());
		}
	}
	
	@Test
	void testAddDocumentsWihtoutNRTTableUnderDeletion() {
		tableUnderDeletion();
		setMockitoTableExists();
		try {
			inputDocumentService.addDocuments(true,tableName, payload);
		}catch(CustomException e) {
			assertEquals(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), e.getExceptionCode());
		}
	}
	
	@Test
	void testPerformDocumentInjectionSuccess() {
		setMockitoTableExists();
		tableNotUnderDeletion();
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityBatch = inputDocumentService.performDocumentInjection(false, tableName, payload, responseDTO);
		assertEquals(200, responseEntityBatch.getStatusCodeValue());
		
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityNRT = inputDocumentService.performDocumentInjection(true, tableName, payload, responseDTO);
		assertEquals(200, responseEntityNRT.getStatusCodeValue());
		
		response.setDocumentUploaded(false);
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
		
	}
	
	@Test
	void testPerformDocumentInjectionBadRequest() {
		setMockitoTableExists();
		tableNotUnderDeletion();
		response.setDocumentUploaded(false);
		Mockito.when(uploadDocumentUtil.softcommit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityBatch = inputDocumentService.performDocumentInjection(false, tableName, payload, responseDTO);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), responseEntityBatch.getStatusCodeValue());
		
		Mockito.when(uploadDocumentUtil.commit()).thenReturn(response);
		ResponseEntity<ThrottlerResponse> responseEntityNRT = inputDocumentService.performDocumentInjection(true, tableName, payload, responseDTO);
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), responseEntityNRT.getStatusCodeValue());	
	}
	
}