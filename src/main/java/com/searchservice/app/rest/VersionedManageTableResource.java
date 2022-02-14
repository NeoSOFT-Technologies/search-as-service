package com.searchservice.app.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTOv2;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.versioned-home}"+"/manage/table")
public class VersionedManageTableResource {

	private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

	private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;

	private ManageTableServicePort manageTableServicePort;

	private TableDeleteServicePort tableDeleteServicePort;

	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort,TableDeleteServicePort tableDeleteServicePort) {
		this.manageTableServicePort = manageTableServicePort;
		this.tableDeleteServicePort = tableDeleteServicePort;
	}

	@GetMapping("/capacity-plans")
    @Operation(summary = "/get-capacity-plans")
    public GetCapacityPlanDTO capacityPlans() {
        log.debug("Get capacity plans");
        GetCapacityPlanDTO getCapacityPlanDTO=manageTableServicePort.capacityPlans();
        return getCapacityPlanDTO;
    }
	
	
    @GetMapping
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO getTables() {
        log.debug("Get all tables");

        ResponseDTO getListItemsResponseDTO=manageTableServicePort.getTables();
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(getListItemsResponseDTO.getResponseStatusCode()==200){
            return getListItemsResponseDTO;
        }else{
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
	@GetMapping("/{clientid}/{tableName}")
	@Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
	public TableSchemaDTOv2 getTable(
			@PathVariable int clientid, 
			@PathVariable String tableName) {
		log.debug("Get table info");

		tableName = tableName + "_" + clientid;
		
		// GET tableDetails
		Map<Object, Object> tableDetailsMap= manageTableServicePort.getTableDetails(tableName);

		// GET tableSchema
		TableSchemaDTOv2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName);
		if (tableInfoResponseDTO == null)
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		
		// SET tableDetails in tableInfoResponseDTO
		tableInfoResponseDTO.setTableDetails(tableDetailsMap);
		if (tableInfoResponseDTO.getStatusCode() == 200) {
			tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
			return tableInfoResponseDTO;
		} else {
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}


    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO createTable(
    		@RequestBody ManageTableDTO manageTableDTO) {
        log.debug("Create table");

        ResponseDTO apiResponseDTO=manageTableServicePort.createTableIfNotPresent(manageTableDTO);
        if(apiResponseDTO.getResponseStatusCode()==200){
        	apiResponseDTO.setResponseMessage("Table: "+manageTableDTO.getTableName()+", is created successfully");
            return apiResponseDTO;
        }else{
        	log.debug("Table could not be created: {}", apiResponseDTO);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

	
	@DeleteMapping("/{clientid}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseDTO deleteTable(
			@PathVariable String tableName, 
			@PathVariable int clientid) {
		log.debug("Delete table");
		tableName = tableName + "_" + clientid;
		if(tableDeleteServicePort.checkTableExistensce(tableName)) {
		    ResponseDTO apiResponseDTO = tableDeleteServicePort.initializeTableDelete(clientid, tableName);
		    if (apiResponseDTO.getResponseStatusCode() == 200) {
			 return apiResponseDTO;
		   } else {
			 log.debug("Exception occurred: {}", apiResponseDTO);
			 throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		   }
		}else {
			throw new BadRequestOccurredException(400, "Table "+tableName+" For Client ID "+clientid+" Does Not Exist");
		}
	}
	
	
	@PutMapping("/{clientId}")
	@Operation(summary = "/undo-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseDTO undoTable(@PathVariable int clientId)
	{	
		log.debug("Undo Table Delete");
		ResponseDTO apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(clientId);
		if(apiResponseDTO.getResponseStatusCode() ==200)
		{
			return apiResponseDTO;
		}
		else
		{
			log.debug("Exception Occured While Performing Undo Delete For Client ID: {} ",clientId);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
	

	@PutMapping("/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseDTO updateTableSchema(
			@PathVariable String tableName,
			@RequestBody TableSchemaDTO newTableSchemaDTO) {
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO);
		if(apiResponseDTO.getResponseStatusCode() == 200)
			return apiResponseDTO;
		else
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
	}
}
