package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;


@Component
public interface ManageTableServicePort {

	/* 
	 * CRUD operations for managing tables
	 */
	// GET request
	GetCapacityPlanDTO capacityPlans();
	ResponseDTO getTables();
	TableSchemaDTO getTableSchemaIfPresent(String tableName);
	Map getTableDetails(String tableName);

	// CREATE requests
	ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO);
	// DELETE requests
	ResponseDTO deleteTable(String tableName);
	// UPDATE requests
	ResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO);
    
    
    /*
     * Auxiliary TableServices
     */
    boolean isConfigSetExists(String configSetName);
    ResponseDTO getConfigSets();
    boolean isTableExists(String tableName);
    TableSchemaDTO getTableSchema(String tableName);
    ResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
    ResponseDTO createTable(ManageTableDTO manageTableDTO);
    TableSchemaDTO addSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    ResponseDTO updateSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    ResponseDTO addAliasTable(String tableOriginalName, String tableAlias);
	ResponseDTO deleteConfigSet(String configSetName);
}
