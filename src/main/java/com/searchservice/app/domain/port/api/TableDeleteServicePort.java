package com.searchservice.app.domain.port.api;

import java.io.File;
import org.springframework.stereotype.Component;
import com.searchservice.app.domain.dto.Response;

@Component
public interface TableDeleteServicePort {

	// Initializing Table for Deletion
	public Response initializeTableDelete(int tenantId, String tableName);

	// Scheduler to Check For Table to Deleted With More or Equal to 15days Request
	// Time
	public int checkDeletionofTable();

	// Undoing Table To Be Deleted
	public Response undoTableDeleteRecord(String tableName);

	// To Check Status of Table Deletion Process
	public boolean checkTableDeletionStatus(int deleteRecordCount);

	public boolean isTableUnderDeletion(String tableName);

	public Response getTablesUnderDeletion(boolean getDeletedTableListWithTenantId);
	
	public Response getTablesUnderDeletionPagination(boolean getDeletedTableListWithTenantId, int pageNumber, int pageSize);

	public boolean checkIfTableDeleteFileExist(File file);
	
	Response getTablesUnderDeletionForTenant(int tenantId, int pageNumber, int pageSize);

}
