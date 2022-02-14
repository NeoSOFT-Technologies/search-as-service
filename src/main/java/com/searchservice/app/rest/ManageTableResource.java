package com.searchservice.app.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}"+"/manage/table")
public class ManageTableResource {

	private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

	private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;

	private ManageTableServicePort manageTableServicePort;

	private TableDeleteServicePort tableDeleteServicePort;

	public ManageTableResource(ManageTableServicePort manageTableServicePort,TableDeleteServicePort tableDeleteServicePort) {
		this.manageTableServicePort = manageTableServicePort;
		this.tableDeleteServicePort = tableDeleteServicePort;
	}

	@GetMapping("/capacity-plans")
    @Operation(summary = "/get-capacity-plans")
    public ResponseEntity<GetCapacityPlan> capacityPlans() {
        log.debug("Get capacity plans");
        GetCapacityPlan getCapacityPlanDTO=manageTableServicePort.capacityPlans();
        return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);
    }
	
	
    @GetMapping("/{clientId}")
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> getTables(@PathVariable int clientId) {
        log.debug("Get all tables");

       Response getListItemsResponseDTO=manageTableServicePort.getTables(clientId);
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
        if(getListItemsResponseDTO.getStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
        }else{
            throw new BadRequestOccurredException(400, ResponseMessages.DEFAULT_EXCEPTION_MSG);
        }
    }
    
    
	@GetMapping("/{clientId}/{tableName}")
	@Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<TableSchemav2> getTable(
			@PathVariable int clientId, 
			@PathVariable String tableName) {
		log.debug("Get table info");

		tableName = tableName + "_" + clientId;
		
		// GET tableDetails
		Map<Object, Object> tableDetailsMap= manageTableServicePort.getTableDetails(tableName);

		// GET tableSchema
		TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName);
		if (tableInfoResponseDTO == null)
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		
		// SET tableDetails in tableInfoResponseDTO
//		tableInfoResponseDTO.setTableDetails(tableDetailsMap);
		if (tableInfoResponseDTO.getStatusCode() == 200) {
			tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(tableInfoResponseDTO);
		} else {
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}


	@PostMapping("/{clientId}")
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> createTable(
			@PathVariable int clientId,
			@RequestBody ManageTable manageTableDTO) {
		log.debug("Create table");
		manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + clientId);
		Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO);
		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO.setMessage("Table- " + manageTableDTO.getTableName().split("_")[0] + ", is created successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.info("Table could not be created: {}", apiResponseDTO);
			throw new BadRequestOccurredException(400, "REST operation could not be performed");
		}
	}

	
	@DeleteMapping("/{clientId}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> deleteTable(
			@PathVariable String tableName, 
			@PathVariable int clientId) {
		log.debug("Delete table");
		tableName = tableName + "_" + clientId;
		if(tableDeleteServicePort.checkTableExistensce(tableName)) {
		    Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(clientId, tableName);
		    if (apiResponseDTO.getStatusCode() == 200) {
			 return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		   } else {
			 log.debug("Exception occurred: {}", apiResponseDTO);
			 throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		   }
		}else {
			throw new BadRequestOccurredException(400, "Table "+tableName+" For Client ID "+clientId+" Does Not Exist");
		}
	}
	
	
	@PutMapping("/{clientId}")
	@Operation(summary = "/undo-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> undoTable(@PathVariable int clientId)
	{	
		log.debug("Undo Table Delete");
		Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(clientId);
		if(apiResponseDTO.getStatusCode() ==200)
		{
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		}
		else
		{
			log.debug("Exception Occured While Performing Undo Delete For Client ID: {} ",clientId);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
	

	@PutMapping("/{clientId}/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> updateTableSchema(
			@PathVariable String tableName, 
			@PathVariable int clientId,
			@RequestBody TableSchema newTableSchemaDTO) {
		tableName = tableName + "_" + clientId;
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

		newTableSchemaDTO.setTableName(tableName);
		Response apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO);
	
		if (apiResponseDTO.getStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		else
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
	}
}
