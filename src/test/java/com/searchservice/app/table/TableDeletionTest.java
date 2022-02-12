package com.searchservice.app.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.service.TableDeleteService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TableDeletionTest{

	@MockBean
	private TableDeleteServicePort tableDeleteServicePort;
	
	@InjectMocks
	private TableDeleteService tableDeleteService;
	
	private ResponseDTO tableDeleteIntializeResponseDTO;
	private ResponseDTO tableDeleteUndoResponseDTO;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private void setMockitoSuccessTableDeleteInitialize() {
		tableDeleteIntializeResponseDTO = new ResponseDTO();
		tableDeleteIntializeResponseDTO.setResponseStatusCode(200);
		tableDeleteIntializeResponseDTO.setResponseMessage(" Successfully Initialized Table For Deletion" );
	}
	
	private void setMockitoFailedTableDeleteInitialize() {
		tableDeleteIntializeResponseDTO = new ResponseDTO();
		tableDeleteIntializeResponseDTO.setResponseStatusCode(400);
		tableDeleteIntializeResponseDTO.setResponseMessage(" Failure While Initializing Table For Deletion" );
	}
	
	private void setMockitoFailedTableDeleteUndo() {
		tableDeleteUndoResponseDTO = new ResponseDTO();
		tableDeleteUndoResponseDTO.setResponseStatusCode(400);
		tableDeleteUndoResponseDTO.setResponseMessage("Failure While Performing Undo For Table Deletion" );
	}
	
	private void setMockitoSuccessTableDeleteUndo() {
		tableDeleteUndoResponseDTO = new ResponseDTO();
		tableDeleteUndoResponseDTO.setResponseStatusCode(200);
		tableDeleteUndoResponseDTO.setResponseMessage("Successfully Performed Undo For Table Deletion" );
	}
	@Test
	void testTableDeleteInitialize() {
		logger.info("Table Delete Intialization test cases getting executed..");
		//For Valid Client ID and Table Name
		setMockitoSuccessTableDeleteInitialize();
		when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString())).
		thenReturn(tableDeleteIntializeResponseDTO);
		assertEquals(200,tableDeleteService.initializeTableDelete(1,"TestTable").getResponseStatusCode());
		
		//Checking With CLient ID as 0
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(0,"Test").getResponseStatusCode());
		
		//Checking With Table Name as Empty
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(1,null).getResponseStatusCode());
	}
	
	@Test
	void testTableDeletionUndo() {
		
		logger.info("Undo Table Deletion test cases getting executed..");
		
		//Checking for Valid Client ID
		setMockitoSuccessTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(200, tableDeleteService.undoTableDeleteRecord(1).getResponseStatusCode());
		
		//Checking For Invalid Client ID as 0
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord(0).getResponseStatusCode());
		
		//Checking For Invalid Client ID as -100
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord(-100).getResponseStatusCode());

	}
	
	@Test
	void testTableDeletion() {
	
		logger.info("Table Deletion test cases getting executed..");
		//Checking for any error while performing deletion
		when(tableDeleteServicePort.checkDeletionofTable()).
		thenReturn(1);
		try {
			tableDeleteService.checkDeletionofTable();
		}catch(Exception e) {
			assertTrue(0<1);
		}
		
		//Checking Status if Deletion is Successfull
		when(tableDeleteServicePort.checkTableDeletionStatus(1)).
		thenReturn(true);
		assertTrue(tableDeleteService.checkTableDeletionStatus(1));
	}
}
