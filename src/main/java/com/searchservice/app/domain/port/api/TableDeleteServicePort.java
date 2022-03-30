package com.searchservice.app.domain.port.api;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;

@Component
public interface TableDeleteServicePort {
	    //Initializing Table for Deletion
		public Response initializeTableDelete(int tenantId,String tableName,LoggersDTO loggersDTO);
		
		//Scheduler to Check For Table to Deleted With More or Equal to 15days Request Time
		public int checkDeletionofTable(LoggersDTO loggersDTO);
		
		//Undoing Table To Be Deleted
		public Response undoTableDeleteRecord(String tableName,LoggersDTO loggersDTO);
		
		//To Check Status of Table Deletion Process
		public boolean checkTableDeletionStatus(int deleteRecordCount);
		
		//To Check if a Table Exist or Not Before Deletion
		public boolean checkTableExistensce(String tableName);
		
		public boolean isTableUnderDeletion(String tableName);
		
		public List<String> getTableUnderDeletion();
		
		public boolean checkIfTableDeleteFileExist(File file);
		
}
