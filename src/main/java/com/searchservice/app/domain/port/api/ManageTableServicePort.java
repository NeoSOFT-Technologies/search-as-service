package com.searchservice.app.domain.port.api;

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
	GetListItemsResponseDTO getTables(String correlationid,String ipaddress);
	GetListItemsResponseDTO getConfigSets();
	TableSchemaResponseDTO getTableSchemaIfPresent(String tableName,String correlationid,String ipaddress);
	// CREATE requests
	ApiResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
	ApiResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO,String correlationid,String ipaddress);
	// DELETE requests
	ApiResponseDTO deleteConfigSet(String configSetName);
	ApiResponseDTO deleteTable(String tableName,String correlationid, String ipaddress);
	// UPDATE requests
	ApiResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO,String correlationid, String ipaddress);
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
