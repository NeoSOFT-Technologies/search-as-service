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
	ResponseDTO getTables(String correlationid, String ipaddress);
	ResponseDTO getConfigSets();
	TableSchemaDTO getTableSchemaIfPresent(String tableName, String correlationid, String ipaddress);
	Map getTableDetails(String tableName, String correlationid, String ipaddress);

	// CREATE requests
	ResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
	ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO, String correlationid, String ipaddress);
	// DELETE requests
	ResponseDTO deleteConfigSet(String configSetName);
	ResponseDTO deleteTable(String tableName, String correlationid, String ipaddress);
	// UPDATE requests
	ResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO, String correlationid, String ipaddress);
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
