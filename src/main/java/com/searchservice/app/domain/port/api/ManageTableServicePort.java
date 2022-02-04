package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;

@Component
public interface ManageTableServicePort {

	/* 
	 * CRUD operations for managing tables
	 */
	// GET request
	GetListItemsResponseDTO getTables();
	GetListItemsResponseDTO getConfigSets();
	TableSchemaResponseDTO getTableSchemaIfPresent(String tableName);
	Map getTableDetails(String tableName);
	// CREATE requests
	ApiResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
	ApiResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO);
	// DELETE requests
	ApiResponseDTO deleteConfigSet(String configSetName);
	ApiResponseDTO deleteTable(String tableName);
	// UPDATE requests
	ApiResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO);
	ApiResponseDTO addAliasTable(String tableOriginalName, String tableAlias);
	// Auxiliary Services
    GetCapacityPlanDTO capacityPlans();
    ApiResponseDTO isTablePresent(String tableName);
    
    /*
     * Auxiliary TableServices
     */
    boolean isConfigSetExists(String configSetName);
    boolean isTableExists(String tableName);
    TableSchemaResponseDTO getTableSchema(String tableName);
    ApiResponseDTO createTable(ManageTableDTO manageTableDTO);
    TableSchemaResponseDTO addSchemaAttributes(TableSchemaDTO tableSchemaDTO);
    ApiResponseDTO updateSchemaAttributes(TableSchemaDTO tableSchemaDTO);
}
