package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTOv2;


@Component
public interface ManageTableServicePort {

	/* 
	 * CRUD operations for managing tables
	 */
	// GET request
	GetCapacityPlanDTO capacityPlans();
	ResponseDTO getTables();
	TableSchemaDTOv2 getCurrentTableSchema(int clientId, String tableName);

	// CREATE requests
	ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO);
	// DELETE requests
	ResponseDTO deleteTable(String tableName);
	// UPDATE requests
	ResponseDTO updateTableSchema(int clientId, String tableName, TableSchemaDTO tableSchemaDTO);
    
    
    /*
     * Auxiliary TableServices
     */
    boolean isConfigSetExists(String configSetName);
    ResponseDTO getConfigSets();
    boolean isTableExists(String tableName);
    TableSchemaDTO getTableSchema(String tableName);
	TableSchemaDTOv2 getTableSchemaIfPresent(int clientId, String tableName);
	Map<Object, Object> getTableDetails(int clientId, String tableName);
    ResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
    ResponseDTO createTable(ManageTableDTO manageTableDTO);
    TableSchemaDTO addSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    // UPDATE
    ResponseDTO initializeDeleteTableSchemaAttributes(
    		TableSchemaDTO tableSchemaDTO, 
    		int clientId, 
    		String tableName);
    TableSchemaDTO compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int clientId, TableSchemaDTO tableSchema);
    ResponseDTO updateSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    ResponseDTO addAliasTable(String tableOriginalName, String tableAlias);
	ResponseDTO deleteConfigSet(String configSetName);
}
