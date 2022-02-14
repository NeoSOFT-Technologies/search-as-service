package com.searchservice.app.domain.port.api;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.Response;

@Component
public interface TableDeleteServicePort {
	    //Initializing Table for Deletion
		public Response initializeTableDelete(int clientId,String tableName);
		
		//Scheduler to Check For Table to Deleted With More or Equal to 15days Request Time
		public int checkDeletionofTable();
		
		//Undoing Table To Be Deleted
		public Response undoTableDeleteRecord(int clientId);
		
		//To Check Status of Table Deletion Process
		public boolean checkTableDeletionStatus(int deleteRecordCount);
		
		//To Check if a Table Exist or Not Before Deletion
		public boolean checkTableExistensce(String tableName);
}
