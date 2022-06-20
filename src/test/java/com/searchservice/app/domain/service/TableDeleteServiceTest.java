package com.searchservice.app.domain.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.DateUtil;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@TestPropertySource(
        properties = {
                "table-delete-duration.days: 15",
                "table-delete-record-file.testPath: src/test/resources/TableDeleteRecordTest.csv"
        }
)
class TableDeleteServiceTest {

	@Value("${table-delete-duration.days}")
	long tableDurationDays;
	
	@Value("${table-delete-record-file.testPath}")
	String deleteRecordFilePath;

	@MockBean
	private TableDeleteServicePort tableDeleteServicePort;

	@MockBean
	private ManageTableServicePort manageTableServicePort;

	@InjectMocks
	private TableDeleteService tableDeleteService;
	
	private   SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	
	
	private Response tableDeleteIntializeResponseDTO;
	private Response tableDeleteResponseDTO;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private void setMockitoSuccessTableDelete() {
		tableDeleteIntializeResponseDTO = new Response();
		tableDeleteIntializeResponseDTO.setStatusCode(200);
		tableDeleteIntializeResponseDTO.setMessage(" Successfully Initialized Table For Deletion");

		tableDeleteResponseDTO = new Response();
		tableDeleteResponseDTO.setStatusCode(200);
		tableDeleteResponseDTO.setMessage("Table Succesfully Deleted");
		Mockito.when(manageTableServicePort.deleteTable(Mockito.anyString())).thenReturn(tableDeleteResponseDTO);
	}
 
	
	@BeforeAll
	void setUp() throws IOException {
		File testFile = new File(deleteRecordFilePath);
		testFile.createNewFile();
		addSampleData(testFile);
		ReflectionTestUtils.setField(tableDeleteService,"deleteRecordFilePath",deleteRecordFilePath);
		ReflectionTestUtils.setField(tableDeleteService,"tableDeleteDuration",tableDurationDays);
	}
	
	@Test
	void testTableDeletion() {
		
		setMockitoSuccessTableDelete();
		int deleteCount = tableDeleteService.checkDeletionofTable();
		assertNotEquals(-1,deleteCount);
		
		//Checking Status if Deletion is Successfull
		when(tableDeleteServicePort.checkTableDeletionStatus(1)).
		thenReturn(true);
		assertTrue(tableDeleteService.checkTableDeletionStatus(1));
	}
	
	void intializeTableforTesting() {
		tableDeleteService.initializeTableDelete(101, "TestTable_101");
	}
	
	@Test
	void testTableDeleteInitializeInvalid() {
		
		logger.info("Table Delete Intialization test cases getting executed..");
		// For Valid Client ID and Table Name

		// Checking With CLient ID as 0
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tableDeleteService.initializeTableDelete(0, "Testing1_0").getStatusCode());

		// Checking With Table Name as Null
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tableDeleteService.initializeTableDelete(101, "").getStatusCode());

	}
	
	@Test
	void testTableDeleteInitializeValid() {
		
		assertEquals(200, tableDeleteService.initializeTableDelete(101, "Testing_101").getStatusCode());

	}
	
	@Test
	void testTableDeleteInitializeInvalidFile() {
		ReflectionTestUtils.setField(tableDeleteService,"deleteRecordFilePath",deleteRecordFilePath+"/Testing");
		assertEquals(400, tableDeleteService.initializeTableDelete(101, "Testing_101").getStatusCode());

	}

	@Test
	void getUndoResponseTest() {
		Response rs = tableDeleteService.getUndoDeleteResponse(2, "Testing");
		assertEquals(200, rs.getStatusCode());
	}

	@Test
	void deletionStatusTestPass() {
		boolean checkDeleteStatus = tableDeleteService.checkTableDeletionStatus(1);
		assertTrue(checkDeleteStatus);
	}

	@Test
	void deletionStatusTestFail() {
		boolean checkDeleteStatus = tableDeleteService.checkTableDeletionStatus(0);
		assertFalse(checkDeleteStatus);
	}

	@Test
	void getTableUndeDeletionTest() {
		Response  tableUnderDeletion = tableDeleteService.getTableUnderDeletion(false);
		Assertions.assertEquals(200, tableUnderDeletion.getStatusCode());
	}
	
	@Test
	void getAllTableUndeDeletionTest() {
		Response  tableUnderDeletion = tableDeleteService.getTableUnderDeletion(true);
		Assertions.assertEquals(200, tableUnderDeletion.getStatusCode());
	}

	@Test
	void checkDeleteFileInvalidPath() {
		boolean b = tableDeleteService.checkIfTableDeleteFileExist(new File(deleteRecordFilePath + "/Testing"));
		assertFalse(b);
	}
	
	@Test
	void checkDeleteFileValidPath() {
		File tesFile = new File(deleteRecordFilePath +"1");
		boolean b = tableDeleteService.checkIfTableDeleteFileExist(tesFile);
		assertTrue(b);
		tesFile.delete();
	}

	@Test
	void invalidTableExist() {
		boolean b = tableDeleteService.isTableUnderDeletion("_Test_101");
		assertFalse(b);
	}
	
	@Test
	void underDeletionTableTest() {
		intializeTableforTesting();
		boolean tableWithTenantID = tableDeleteService.isTableUnderDeletion("TestTable");
		assertTrue(tableWithTenantID);
		
		boolean tableWithoutTenantID = tableDeleteService.isTableUnderDeletion("TestTable_101");
		assertTrue(tableWithoutTenantID);
	}

	@Test
	void performTableDeleteTest() {
		setMockitoSuccessTableDelete();
		assertTrue(tableDeleteService.performTableDeletion("1,Testing"));
	}
	
	@Test
	void performTableDeleteTestException() {
		Mockito.when(manageTableServicePort.deleteTable(Mockito.anyString())).thenThrow(new CustomException(108,HttpStatusCode.TABLE_NOT_FOUND,""));
		assertFalse(tableDeleteService.performTableDeletion("1,Testing90"));
	}
	
	@Test
     void testTableDeletionUndoBadRequest() {
			//Checking For Invalid Table Name As Empty String 
			assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), tableDeleteService.undoTableDeleteRecord(null).getStatusCode());
					
	}
	
	@Test
	void getUndoDeleteResponseTestBadRequest() {
		Response rs = tableDeleteService.getUndoDeleteResponse(0, "Testing");
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), rs.getStatusCode());
	}
	
	@Test
	void isTableExsist() {
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		boolean b = manageTableServicePort.isTableExists("Testing101");
		assertTrue(b);
	}

	@Test
	void performUndoTableDeletionSuccess() {
		intializeTableforTesting();
		Response undoResponse =tableDeleteService.performUndoTableDeletion("TestTable_101");
		assertEquals(200,undoResponse.getStatusCode());
	}
	
	
	@Test
	void performUndoTableDeletionBadRequest() {
		Response undoResponse =tableDeleteService.performUndoTableDeletion("TestTable_101");
		assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),undoResponse.getStatusCode());
	}
	
	public void addSampleData(File file) {
		 do {	
		  try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw);) {
				bw.write("TenantID,TableName,RequestTime\n");
				bw.write("101,Testing_101,17-3-2022 12:56:56\n");
				bw.write("102,Testing_102,"+DateUtil.getFormattedDate(formatter)+"\n");
			}catch (Exception e) {
				e.printStackTrace();		
			} 
		 }while(false);
	}
	
	@AfterAll
	@Order(1)
	void getTableUndeDeletionTestInvalid() {
		testTableDeleteInitializeInvalidFile();
		Response  tableUnderDeletion = tableDeleteService.getTableUnderDeletion(false);
		Assertions.assertEquals(400, tableUnderDeletion.getStatusCode());
	}
	
	@AfterAll
	@Order(2)
	void deleteAllTestFiles() {
		File file = new File("src/test/resources");
		for(File f: file.listFiles()) {
			if(f.toString().endsWith(".csv")) {
				f.delete();
			}
		}
	}
	}

