package com.searchservice.app.domain.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TableDeleteServiceTest {

	@Value("${table-delete-file.path}")
	String deleteRecordFilePath;

	@MockBean
	private TableDeleteServicePort tableDeleteServicePort;

	@MockBean
	private ManageTableServicePort manageTableServicePort;

	@InjectMocks
	private TableDeleteService tableDeleteService;

	private Response tableDeleteIntializeResponseDTO;
	private Response tableDeleteUndoResponseDTO;
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

   
	@Test
	void testTableDeletion() {
		setMockitoSuccessTableDelete();
		int deleteCount = tableDeleteService.checkDeletionofTable();
		assertNotNull(deleteCount);
		//Checking Status if Deletion is Successfull
		when(tableDeleteServicePort.checkTableDeletionStatus(1)).
		thenReturn(true);
		assertTrue(tableDeleteService.checkTableDeletionStatus(1));
	}
	
	void intializeTableforUndoDeletion() {
		tableDeleteService.initializeTableDelete(101, "TestTable_101");
	}
	@Test
	void testTableDeleteInitialize() {
		logger.info("Table Delete Intialization test cases getting executed..");
		// For Valid Client ID and Table Name

		// Checking With CLient ID as 0
		assertEquals(400, tableDeleteService.initializeTableDelete(0, "Testing1_0").getStatusCode());

		// Checking With Table Name as Null
		assertEquals(400, tableDeleteService.initializeTableDelete(101, "").getStatusCode());

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
		List<String> tableUnderDeletion = tableDeleteService.getTableUnderDeletion();
		Assertions.assertNotNull(tableUnderDeletion);
	}

	@Test
	void checkDeleteFileInvalidPath() {
		boolean b = tableDeleteService.checkIfTableDeleteFileExist(new File(deleteRecordFilePath + "/Testing"));
		assertFalse(b);
	}

	@Test
	void invalidTableExist() {
		boolean b = tableDeleteService.isTableUnderDeletion("_Test_101");
		assertFalse(b);
	}

	@Test
	void performTableDeleteTest() {
		setMockitoSuccessTableDelete();
		assertTrue(tableDeleteService.performTableDeletion("1,Testing"));
	}
	
	@Test
     void testTableDeletionUndo() {
			//Checking For Invalid Table Name As Empty String 
			assertEquals(400, tableDeleteService.undoTableDeleteRecord(null).getStatusCode());
					
		}
	
	@Test
	void getUndoDeleteResponseTestBadRequest() {
		Response rs = tableDeleteService.getUndoDeleteResponse(0, "Testing");
		assertEquals(400, rs.getStatusCode());
	}
	
	@Test
	void isTableExsist() {
		Mockito.when(manageTableServicePort.isTableExists(Mockito.anyString())).thenReturn(true);
		boolean b = tableDeleteService.checkTableExistensce("Testing101");
		assertTrue(b);
	}

	@Test
	void performUndoTableDeletionSuccess() {
		
		intializeTableforUndoDeletion();
		Response undoResponse =tableDeleteService.performUndoTableDeletion("TestTable_101");
		assertEquals(200,undoResponse.getStatusCode());
	}
	
	@Test
	void performUndoTableDeletionBadRequest() {
		Response undoResponse =tableDeleteService.performUndoTableDeletion("TestTable_101");
		assertEquals(400,undoResponse.getStatusCode());
	}
}
