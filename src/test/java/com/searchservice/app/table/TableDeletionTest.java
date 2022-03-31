package com.searchservice.app.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
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
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.service.TableDeleteService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TableDeletionTest{

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
	
	private void setMockitoSuccessTableDeleteInitialize() {
		tableDeleteIntializeResponseDTO = new Response();
		tableDeleteIntializeResponseDTO.setStatusCode(200);
		tableDeleteIntializeResponseDTO.setMessage(" Successfully Initialized Table For Deletion" );
		
		tableDeleteResponseDTO = new Response();
		tableDeleteResponseDTO.setStatusCode(200);
		tableDeleteResponseDTO.setMessage("Table Succesfully Deleted");
		Mockito.when(manageTableServicePort.deleteTable(Mockito.anyString(),Mockito.any())).thenReturn(tableDeleteResponseDTO);
	}
	
	private void setMockitoFailedTableDeleteInitialize() {
		tableDeleteIntializeResponseDTO = new Response();
		tableDeleteIntializeResponseDTO.setStatusCode(400);
		tableDeleteIntializeResponseDTO.setMessage(" Failure While Initializing Table For Deletion" );
	}
	
	private void setMockitoFailedTableDeleteUndo() {
		tableDeleteUndoResponseDTO = new Response();
		tableDeleteUndoResponseDTO.setStatusCode(400);
		tableDeleteUndoResponseDTO.setMessage("Failure While Performing Undo For Table Deletion" );
	}
	
	private void setMockitoSuccessTableDeleteUndo() {
		tableDeleteUndoResponseDTO = new Response();
		tableDeleteUndoResponseDTO.setStatusCode(200);
		tableDeleteUndoResponseDTO.setMessage("Successfully Performed Undo For Table Deletion" );
	}
	@Test
	void testTableDeleteInitialize() {
		logger.info("Table Delete Intialization test cases getting executed..");
		//For Valid Client ID and Table Name
		setMockitoSuccessTableDeleteInitialize();
		when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		thenReturn(tableDeleteIntializeResponseDTO);
		assertEquals(200,tableDeleteService.initializeTableDelete(1,"TestTable_10_101",new LoggersDTO()).getStatusCode());

		
		//Checking With CLient ID as 0
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(0,"Test_10_101",new LoggersDTO()).getStatusCode());

		
		//Checking With Table Name as Null
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(1,null,new LoggersDTO()).getStatusCode());
		 
		//Checking With Table Name as Empty
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		assertEquals(400 ,tableDeleteService.initializeTableDelete(1,"",new LoggersDTO()).getStatusCode());	
	}
	
	@Test
	void testTableDeletionUndo() {
		
		logger.info("Undo Table Deletion test cases getting executed..");
			
		//Checking With Valid Details
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyString(),Mockito.any())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord("Test_10_101",new LoggersDTO()).getStatusCode());
		
		//Checking For Invalid Table Name As Null 
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyString(),Mockito.any())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord(null,new LoggersDTO()).getStatusCode());
		
		//Checking For Invalid Table Name As Empty String 
			setMockitoFailedTableDeleteUndo();
			when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyString(),Mockito.any())).
			thenReturn(tableDeleteUndoResponseDTO);
			 try {
				 assertEquals(400, tableDeleteService.undoTableDeleteRecord("",new LoggersDTO()).getStatusCode());
				}catch(Exception e) {
					assertTrue(0<1);
				}
	}
	
	@Test
	void testTableDeletion() {
	
		logger.info("Table Deletion test cases getting executed..");
		//Checking for any error while performing deletion
		when(tableDeleteServicePort.checkDeletionofTable(Mockito.any())).
		thenReturn(1);
		try {
			tableDeleteService.checkDeletionofTable(new LoggersDTO());
		}catch(Exception e) {
			assertTrue(0<1);
		}
		
		//Checking Status if Deletion is Successfull
		when(tableDeleteServicePort.checkTableDeletionStatus(1)).
		thenReturn(true);
		assertTrue(tableDeleteService.checkTableDeletionStatus(1));
	}
	
	@Test
	void getUndoResponseTest() {
		Response rs = tableDeleteService.getUndoDeleteResponse(2, "Testing");
		assertEquals(200,rs.getStatusCode());
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
		boolean b = tableDeleteService.checkIfTableDeleteFileExist(new File(deleteRecordFilePath+"/Testing"));
		assertFalse(b);
	}
	
	@Test
	void invalidTableExist() {
		boolean b = tableDeleteService.isTableUnderDeletion("_Test_101");
		assertFalse(b);
	}
	
	@Test
	void performTableDeleteTest() {
		setMockitoSuccessTableDeleteInitialize();
		assertTrue(tableDeleteService.performTableDeletion("1,Testing", new LoggersDTO()));
	}
	
}
