package com.searchservice.app.rest;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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
import com.searchservice.app.domain.dto.logger.CorrelationID;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
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
    
    CorrelationID correlationID = new CorrelationID();

	@Autowired
	HttpServletRequest request;

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
        GetCapacityPlanDTO getCapacityPlanDTO=manageTableServicePort.capacityPlans(correlationid,ipaddress);
        if(getCapacityPlanDTO.getPlans() != null)
        	return getCapacityPlanDTO;
        else
        	throw new NullPointerOccurredException(500, DEFAULT_EXCEPTION_MSG);
    }
	
	
    @GetMapping
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO getTables() {
        log.debug("Get all tables");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
		
        ResponseDTO getListItemsResponseDTO=manageTableServicePort.getTables(correlationid,ipaddress);
        
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(getListItemsResponseDTO.getResponseStatusCode()==200){
        	log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return getListItemsResponseDTO;
        }else{
        	log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
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
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
		
        TableSchemaDTO tableSchemaResponseDTO=manageTableServicePort.getTableSchemaIfPresent(tableName,correlationid,ipaddress);        
        if(tableSchemaResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(tableSchemaResponseDTO.getStatusCode()==200){
        	log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return tableSchemaResponseDTO;
        }else{
        	log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    
    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO createTable(
    		@RequestBody ManageTableDTO manageTableDTO) {
        log.debug("Create table");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
        ResponseDTO apiResponseDTO=manageTableServicePort.createTableIfNotPresent(manageTableDTO,correlationid,ipaddress);
        if(apiResponseDTO.getResponseStatusCode()==200){
        	log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        	apiResponseDTO.setResponseMessage("Table: "+manageTableDTO.getTableName()+", is created successfully");
            return apiResponseDTO;
        }else{
        	log.debug("Table could not be created: {}", apiResponseDTO);
        	log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
    @DeleteMapping("/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseDTO deleteTable(@PathVariable String tableName) {
        log.debug("Delete table");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
        ResponseDTO apiResponseDTO=manageTableServicePort.deleteTable(tableName,correlationid,ipaddress);

        if(apiResponseDTO.getResponseStatusCode()==200){
        	log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return apiResponseDTO;
        }else{
        	log.debug("Exception occurred: {}", apiResponseDTO);
        	log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
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
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid);
		String ipaddress = request.getRemoteAddr();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename, username, correlationid, ipaddress, timestamp, nameofCurrMethod);
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO,correlationid,ipaddress);
		if(apiResponseDTO.getResponseStatusCode() == 200) {
			log.info("-----------Successfully response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			return apiResponseDTO;
		}
		else {
			log.info("-----------Failed response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
}
