package com.searchservice.app.domain.port.api;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableInfo;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;

@Component
@Repository
public interface ManageTableServicePort {

	/*
	 * CRUD operations for managing tables
	 */
	// GET request

	CapacityPlanResponse capacityPlans();
	
	Response getAllTables(int startRecord, int pageSize);
	
	Response getTables(int tenantId);

	TableSchema getCurrentTableSchema(int tenantId, String tableName);
	
	// CREATE requests
	Response createTableIfNotPresent(CreateTable manageTableDTO);

	// DELETE requests
	Response deleteTable(String tableName);

	// UPDATE requests
	Response updateTableSchema(int tenantId, String tableName, ManageTable tableSchemaDTO);

	/*
	 * Auxiliary TableServices
	 */
	TableSchema getTableSchema(String tableName);

	TableInfo getTableDetails(String tableName);
	
	Response createTable(CreateTable manageTableDTO);

	Response addSchemaFields(ManageTable tableSchemaDTO);

	Response updateSchemaFields(ManageTable tableSchemaDTO);

	Response addAliasTable(String tableOriginalName, String tableAlias);

	// UPDATE Table additional methods
	TableSchema compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int tenantId,
			TableSchema tableSchema);

	// Validation methods
	boolean isTableExists(String tableName);
	
	boolean checkIfTableNameisValid(String tableName);
	
	boolean isColumnNameValid(List<SchemaField> columns);
	
	Boolean isValidFormatDataTypeForMultivalued(List<SchemaField> columns);
	
	void checkForSchemaDeletion();

}
