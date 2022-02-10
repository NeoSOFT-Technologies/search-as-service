package com.searchservice.app.domain.port.api;

import java.util.Map;

import org.springframework.stereotype.Component;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
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
	ResponseDTO getTables(LoggersDTO loggersDTO);
	ResponseDTO getConfigSets();
	TableSchemaDTO getTableSchemaIfPresent(String tableName, LoggersDTO loggersDTO);
	Map<?, ?> getTableDetails(String tableName, LoggersDTO loggersDTO);

	// CREATE requests
	ResponseDTO createConfigSet(ConfigSetDTO configSetDTO);
	ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO,LoggersDTO loggersDTO);
	// DELETE requests
	ResponseDTO deleteConfigSet(String configSetName);
	ResponseDTO deleteTable(String tableName,LoggersDTO loggersDTO);
	// UPDATE requests
	ResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO, LoggersDTO loggersDTO);
	ResponseDTO addAliasTable(String tableOriginalName, String tableAlias);
	// Auxiliary Services
    GetCapacityPlanDTO capacityPlans(LoggersDTO loggersDTO);
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
