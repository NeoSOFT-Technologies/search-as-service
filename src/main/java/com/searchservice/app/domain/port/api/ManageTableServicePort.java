package com.searchservice.app.domain.port.api;

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
	ResponseDTO getTables();
	ResponseDTO getConfigSets();
	TableSchemaDTO getTableSchemaIfPresent(String tableName);
	// CREATE requests
	ResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
	ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO);
	// DELETE requests
	ResponseDTO deleteConfigSet(String configSetName);
	ResponseDTO deleteTable(String tableName);
	// UPDATE requests
	ResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO);
	ResponseDTO addAliasTable(String tableOriginalName, String tableAlias);
	// Auxiliary Services
    GetCapacityPlanDTO capacityPlans();
    ResponseDTO isTablePresent(String tableName);
    
    /*
     * Auxiliary TableServices
     */
    boolean isConfigSetExists(String configSetName);
    boolean isTableExists(String tableName);
    TableSchemaDTO getTableSchema(String tableName);
    ResponseDTO createTable(ManageTableDTO manageTableDTO);
    TableSchemaDTO addSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    ResponseDTO updateSchemaAttributes(TableSchemaDTO tableSchemaDTO);
}
