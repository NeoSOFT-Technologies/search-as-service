package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;

@Component
public interface ManageTableServicePort {

	/*
	 * CRUD operations for managing tables
	 */
	// GET request

	GetCapacityPlan capacityPlans();

	Response getTables(int tenantId);

	TableSchemav2 getCurrentTableSchema(int tenantId, String tableName);

	// CREATE requests
	Response createTableIfNotPresent(ManageTable manageTableDTO);

	// DELETE requests
	Response deleteTable(String tableName);

	// UPDATE requests
	Response updateTableSchema(int tenantId, String tableName, TableSchema tableSchemaDTO);

	/*
	 * Auxiliary TableServices
	 */
	boolean isConfigSetExists(String configSetName);

	Response getConfigSets();

	boolean isTableExists(String tableName);

	TableSchemav2 getTableSchemaIfPresent(String tableName);

	// For Future
	//Map<Object, Object> getTableDetails(String tableName);

	TableSchemav2 getTableSchema(String tableName);

	Response createConfigSet(ConfigSet configSetDTO);

	Response createTable(ManageTable manageTableDTO);

	Response addSchemaAttributes(TableSchema tableSchemaDTO);

	Response updateSchemaAttributes(TableSchema tableSchemaDTO);

	Response addAliasTable(String tableOriginalName, String tableAlias);

	Response deleteConfigSet(String configSetName);

	// UPDATE Table additional methods
	TableSchemav2 compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int tenantId,
			TableSchemav2 tableSchema);

	boolean checkIfTableNameisValid(String tableName);

}
