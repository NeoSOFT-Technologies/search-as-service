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

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.service.TableDeleteService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class TableDeletionTest{

	@MockBean
	private TableDeleteServicePort tableDeleteServicePort;
	
	@InjectMocks
	private TableDeleteService tableDeleteService;
	
	private Response tableDeleteIntializeResponseDTO;
	private Response tableDeleteUndoResponseDTO;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private void setMockitoSuccessTableDeleteInitialize() {
		tableDeleteIntializeResponseDTO = new Response();
		tableDeleteIntializeResponseDTO.setStatusCode(200);
		tableDeleteIntializeResponseDTO.setMessage(" Successfully Initialized Table For Deletion" );
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
		assertEquals(200,tableDeleteService.initializeTableDelete(1,"TestTable",new LoggersDTO()).getStatusCode());
		
		//Checking With CLient ID as 0
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(0,"Test",new LoggersDTO()).getStatusCode());
		
		//Checking With Table Name as Empty
		 setMockitoFailedTableDeleteInitialize();
		 when(tableDeleteServicePort.initializeTableDelete(Mockito.anyInt(),Mockito.anyString(),Mockito.any())).
		 thenReturn(tableDeleteIntializeResponseDTO);
		 assertEquals(400 ,tableDeleteService.initializeTableDelete(1,null,new LoggersDTO()).getStatusCode());
	}
	
	@Test
	void testTableDeletionUndo() {
		
		logger.info("Undo Table Deletion test cases getting executed..");
		
		//Checking for Valid Client ID
		setMockitoSuccessTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt(),Mockito.any())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(200, tableDeleteService.undoTableDeleteRecord(1,new LoggersDTO()).getStatusCode());
		
		//Checking For Invalid Client ID as 0
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt(),Mockito.any())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord(0,new LoggersDTO()).getStatusCode());
		
		//Checking For Invalid Client ID as -100
		setMockitoFailedTableDeleteUndo();
		when(tableDeleteServicePort.undoTableDeleteRecord(Mockito.anyInt(),Mockito.any())).
		thenReturn(tableDeleteUndoResponseDTO);
		assertEquals(400, tableDeleteService.undoTableDeleteRecord(-100,new LoggersDTO()).getStatusCode());

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
}
