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
    public ResponseEntity<GetCapacityPlanDTO> capacityPlans() {
        log.debug("Get capacity plans");
        GetCapacityPlanDTO getCapacityPlanDTO=manageTableServicePort.capacityPlans();
        return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);
    }
	
	
    @GetMapping
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ResponseDTO> getTables() {
        log.debug("Get all tables");

       ResponseDTO getListItemsResponseDTO=manageTableServicePort.getTables();
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
        if(getListItemsResponseDTO.getResponseStatusCode()==200){
            return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
        }else{
            throw new BadRequestOccurredException(400, ResponseMessages.DEFAULT_EXCEPTION_MSG);
        }
    }
    
    
	@GetMapping("/{clientid}/{tableName}")
	@Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<TableSchemaDTOv2> getTable( 
			@PathVariable String tableName, 
			@PathVariable int clientid) {
		log.info("Get table info");

		// GET tableSchema
		TableSchemaDTOv2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(clientid, tableName);
		if (tableInfoResponseDTO == null)
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		
		// GET tableDetails
		Map<Object, Object> tableDetailsMap= manageTableServicePort.getTableDetails(tableName, clientid);
		
		// SET tableDetails in tableInfoResponseDTO
		tableInfoResponseDTO.setTableDetails(tableDetailsMap);
		if (tableInfoResponseDTO.getStatusCode() == 200) {
			tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(tableInfoResponseDTO);
		} else {
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}


	@PostMapping("/{clientid}")
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> createTable(
			@PathVariable int clientid,
			@RequestBody ManageTableDTO manageTableDTO) {
		log.debug("Create table");
		manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + clientid);
		ResponseDTO apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO);
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			apiResponseDTO.setResponseMessage("Table- " + manageTableDTO.getTableName() + ", is created successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.info("Table could not be created: {}", apiResponseDTO);
			throw new BadRequestOccurredException(400, "REST operation could not be performed");
		}
	}

	
	@DeleteMapping("/{clientid}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> deleteTable(
			@PathVariable String tableName, 
			@PathVariable int clientid) {
		log.debug("Delete table");
		tableName = tableName + "_" + clientid;
		if(tableDeleteServicePort.checkTableExistensce(tableName)) {
		    ResponseDTO apiResponseDTO = tableDeleteServicePort.initializeTableDelete(clientid, tableName);
		    if (apiResponseDTO.getResponseStatusCode() == 200) {
			 return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
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
	public ResponseEntity<ResponseDTO> undoTable(@PathVariable int clientId)
	{	
		log.debug("Undo Table Delete");
		ResponseDTO apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(clientId);
		if(apiResponseDTO.getResponseStatusCode() ==200)
		{
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		}
		else
		{
			log.debug("Exception Occured While Performing Undo Delete For Client ID: {} ",clientId);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
	

	@PutMapping("/{clientid}/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> updateTableSchema(
			@PathVariable int clientid, 
			@PathVariable String tableName, 
			@RequestBody TableSchemaDTO newTableSchemaDTO) {
		log.info("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

		newTableSchemaDTO.setTableName(tableName+ "_" +clientid);
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(
				clientid, tableName, newTableSchemaDTO);
	
		if (apiResponseDTO.getResponseStatusCode() == 200)
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		else
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
	}
}
