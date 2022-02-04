package com.searchservice.app.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("${base-url.api-endpoint.manage-table}")
public class VersionedManageTableResource {

    private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

    @Value("${base-url.api-endpoint.manage-table}")
    private String baseUrlManageTable;
    @Value("${saas-ms.request-header.api-version}")
	private static String saasVersionHeader;
    
    private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;
    private static final String DEFAULT_EXCEPTION_MSG = ResponseMessages.DEFAULT_EXCEPTION_MSG;
    
    private ManageTableServicePort manageTableServicePort;
	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
	}
	
	
	@GetMapping("/capacity-plans")
    @Operation(summary = "/get-capacity-plans")
    public GetCapacityPlanDTO capacityPlans() {
        log.debug("Get capacity plans");
        GetCapacityPlanDTO getCapacityPlanDTO=manageTableServicePort.capacityPlans();
        if(getCapacityPlanDTO.getPlans() != null)
        	return getCapacityPlanDTO;
        else
        	throw new NullPointerOccurredException(500, DEFAULT_EXCEPTION_MSG);
    }
	
	
    @GetMapping
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public GetListItemsResponseDTO getTables() {
        log.debug("Get all tables");

        GetListItemsResponseDTO getListItemsResponseDTO=manageTableServicePort.getTables();
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(getListItemsResponseDTO.getStatusCode()==200){
            return getListItemsResponseDTO;
        }else{
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    
    @GetMapping("/schema/{tableName}")
    @Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public TableSchemaResponseDTO getTableSchema(
    		//@RequestHeader(name = SAAS_VERSION_HEADER, defaultValue = "1") String apiVersion, 
    		@PathVariable String tableName) {
        log.debug("Get table schema");

        TableSchemaResponseDTO tableSchemaResponseDTO=manageTableServicePort.getTableSchemaIfPresent(tableName);        
        if(tableSchemaResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(tableSchemaResponseDTO.getStatusCode()==200){
            return tableSchemaResponseDTO;
        }else{
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    
    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponseDTO createTable(
    		@RequestBody ManageTableDTO manageTableDTO) {
        log.debug("Create table");

        ApiResponseDTO apiResponseDTO=manageTableServicePort.createTableIfNotPresent(manageTableDTO);
        if(apiResponseDTO.getResponseStatusCode()==200){
        	apiResponseDTO.setResponseMessage("Table: "+manageTableDTO.getTableName()+", is created successfully");
            return apiResponseDTO;
        }else{
        	log.debug("Table could not be created: {}", apiResponseDTO);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
    @DeleteMapping("/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ApiResponseDTO deleteTable(@PathVariable String tableName) {
        log.debug("Delete table");

        ApiResponseDTO apiResponseDTO=manageTableServicePort.deleteTable(tableName);

        if(apiResponseDTO.getResponseStatusCode()==200){
            return apiResponseDTO;
        }else{
        	log.debug("Exception occurred: {}", apiResponseDTO);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
	@PutMapping("/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ApiResponseDTO updateTableSchema(
			@PathVariable String tableName,
			@RequestBody TableSchemaDTO newTableSchemaDTO) {
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);
		ApiResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO);
		if(apiResponseDTO.getResponseStatusCode() == 200)
			return apiResponseDTO;
		else
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
	}
}
