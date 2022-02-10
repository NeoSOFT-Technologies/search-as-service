package com.searchservice.app.rest;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@RestController
@RequestMapping("${base-url.api-endpoint.versioned-home}"+"/manage/table")
public class VersionedManageTableResource {

    private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

    @Value("${saas-ms.request-header.api-version}")
	private static String saasVersionHeader;
    
    private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;
    private static final String DEFAULT_EXCEPTION_MSG = ResponseMessages.DEFAULT_EXCEPTION_MSG;
    
	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";

    
    private ManageTableServicePort manageTableServicePort;
	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
	}
	
	
	@GetMapping("/capacity-plans")
    @Operation(summary = "/get-capacity-plans")
    public GetCapacityPlanDTO capacityPlans(String correlationid, String ipaddress ) {
        log.debug("Get capacity plans");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        GetCapacityPlanDTO getCapacityPlanDTO=manageTableServicePort.capacityPlans(loggersDTO);

        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
        if(getCapacityPlanDTO.getPlans() != null) {
        	LoggerUtils.printlogger(loggersDTO,false,false);
        	return getCapacityPlanDTO;
        }
        else {
        	LoggerUtils.printlogger(loggersDTO,false,true);
        	throw new NullPointerOccurredException(500, DEFAULT_EXCEPTION_MSG);
        }
    }
	
	@GetMapping
	@Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseDTO getTables() {
		log.debug("Get all tables");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		ResponseDTO getListItemsResponseDTO = manageTableServicePort.getTables(loggersDTO);

        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (getListItemsResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (getListItemsResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO,false,false);
			return getListItemsResponseDTO;
		} else {
			LoggerUtils.printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
    
    @GetMapping("/schema/{tableName}")
    @Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public TableSchemaDTO getTableSchema(
    		//@RequestHeader(name = SAAS_VERSION_HEADER, defaultValue = "1") String apiVersion, 
    		@PathVariable String tableName) {
        log.debug("Get table schema");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        TableSchemaDTO tableSchemaResponseDTO=manageTableServicePort.getTableSchemaIfPresent(tableName,loggersDTO);        

        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
        if(tableSchemaResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(tableSchemaResponseDTO.getStatusCode()==200){
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return tableSchemaResponseDTO;
        }else{
        	LoggerUtils.printlogger(loggersDTO,false,true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    
    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO createTable(
    		@RequestBody ManageTableDTO manageTableDTO) {
        log.debug("Create table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        ResponseDTO apiResponseDTO=manageTableServicePort.createTableIfNotPresent(manageTableDTO,loggersDTO);

        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
        if(apiResponseDTO.getResponseStatusCode()==200){
        	LoggerUtils.printlogger(loggersDTO,false,false);
        	apiResponseDTO.setResponseMessage("Table: "+manageTableDTO.getTableName()+", is created successfully");
            return apiResponseDTO;
        }else{
        	log.debug("Table could not be created: {}", apiResponseDTO);
        	LoggerUtils.printlogger(loggersDTO,false,true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
    @DeleteMapping("/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO deleteTable(@PathVariable String tableName) {
        log.debug("Delete table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        ResponseDTO apiResponseDTO=manageTableServicePort.deleteTable(tableName,loggersDTO);
        
        loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
        if(apiResponseDTO.getResponseStatusCode()==200){
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return apiResponseDTO;
        }else{
        	log.debug("Exception occurred: {}", apiResponseDTO);
        	LoggerUtils.printlogger(loggersDTO,false,true);
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

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO,loggersDTO);
		
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if(apiResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO,false,false);
			return apiResponseDTO;
		}
		else {
			LoggerUtils.printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
}
