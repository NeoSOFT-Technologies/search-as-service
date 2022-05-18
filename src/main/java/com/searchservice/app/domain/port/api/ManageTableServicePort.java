package com.searchservice.app.domain.port.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;

@Component
public interface ManageTableServicePort {

	/*
	 * CRUD operations for managing tables
	 */
	// GET request

	CapacityPlanResponse capacityPlans();

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

	boolean isTableExists(String tableName);

	TableSchema getTableSchema(String tableName);

	Response createTable(CreateTable manageTableDTO);

	Response addSchemaFields(ManageTable tableSchemaDTO);

	Response updateSchemaFields(ManageTable tableSchemaDTO);

	Response addAliasTable(String tableOriginalName, String tableAlias);

	// UPDATE Table additional methods
	TableSchema compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int tenantId,
			TableSchema tableSchema);

	boolean checkIfTableNameisValid(String tableName);
	
	boolean isColumnNameValid(List<SchemaField> columns);
	
	Boolean isValidFormatDataTypeForMultivalued(List<SchemaField> columns);
	
	void checkForSchemaDeletion();

}
