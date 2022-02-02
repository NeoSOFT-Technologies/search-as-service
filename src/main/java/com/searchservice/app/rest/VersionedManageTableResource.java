package com.searchservice.app.rest;


import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.TableSchemaResponseDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedFeatures;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("${base-url.api-endpoint.manage-table}")
public class VersionedManageTableResource {

    private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

    private String saasApiVersion = "1";
    @Value("${base-url.api-endpoint.manage-table}")
    private String BASE_URL_MANAGE_TABLE;
    @Value("${saas-ms.request-header.api-version}")
	private static String SAAS_VERSION_HEADER;
    
    private static final String BAD_REQUEST_MSG = "REST call could not be performed";
    private static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";
    
    private ManageTableServicePort manageTableServicePort;
	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
	}

	
    @ModelAttribute
    public void setResponseHeader(HttpServletResponse response) {
    	saasApiVersion = VersionedFeatures.BASE_URL_MANAGE_TABLE;
        response.setHeader(SAAS_VERSION_HEADER, saasApiVersion);
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
            throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
        }
    }

    
    @GetMapping("/schema/{tableName}")
    @Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public TableSchemaResponseDTO getSchema(
    		//@RequestHeader(name = SAAS_VERSION_HEADER, defaultValue = "1") String apiVersion, 
    		@PathVariable String tableName) {
        log.debug("Get table schema");
        
        
        
        TableSchemaResponseDTO tableSchemaResponseDTO=manageTableServicePort.getTableSchemaIfPresent(tableName);
        
        // testing
		// testing
		System.out.println("base urllllllllllAPI :: "+BASE_URL_MANAGE_TABLE);
        System.out.println("TSR1 >>>>> :: "+tableSchemaResponseDTO);
        
        if(tableSchemaResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(tableSchemaResponseDTO.getStatusCode()==200){
            return tableSchemaResponseDTO;
        }else{
            throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
        }
    }
}
