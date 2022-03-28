package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
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
	GetCapacityPlan capacityPlans(LoggersDTO loggersDTO);
	Response getTables(int clientid,LoggersDTO loggersDTO);
	TableSchemav2 getCurrentTableSchema(int clientId, String tableName, LoggersDTO loggersDTO);

	// CREATE requests
	Response createTableIfNotPresent(ManageTable manageTableDTO,LoggersDTO loggersDTO);
	// DELETE requests
	Response deleteTable(String tableName,LoggersDTO loggersDTO);
	// UPDATE requests
	Response updateTableSchema(int clientId, String tableName, TableSchema tableSchemaDTO,LoggersDTO loggersDTO);
    
    
    /*
     * Auxiliary TableServices
     */
    boolean isConfigSetExists(String configSetName);
    Response getConfigSets();
    boolean isTableExists(String tableName);
	TableSchemav2 getTableSchemaIfPresent(String tableName,LoggersDTO loggersDTO);
	Map<Object, Object> getTableDetails(String tableName,LoggersDTO loggersDTO);
    TableSchemav2 getTableSchema(String tableName);
    Response createConfigSet(ConfigSet configSetDTO);
    Response createTable(ManageTable manageTableDTO);
    Response addSchemaAttributes(TableSchema tableSchemaDTO);
    Response updateSchemaAttributes(TableSchema tableSchemaDTO);
    Response addAliasTable(String tableOriginalName, String tableAlias);
	Response deleteConfigSet(String configSetName);
    // UPDATE Table additional methods
    TableSchemav2 compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(
    		String tableName, int clientId, TableSchemav2 tableSchema);
    boolean checkIfTableNameisValid(String tableName);
 
}
