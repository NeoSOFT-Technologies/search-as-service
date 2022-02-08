package com.searchservice.app.rest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.searchservice.app.domain.dto.ApiResponseDTO;
import com.searchservice.app.domain.dto.GetListItemsResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
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
@RequestMapping("/api/v1/manage/table")
public class ManageTableResource {

    private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

    private static final String BAD_REQUEST_MSG = "REST call could not be performed";
    private static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";
 
    CorrelationID correlationID=new CorrelationID();
    
    @Autowired
    HttpServletRequest request;
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Manage_Table_Resource";
    
    private String username = "Username";  
    private ManageTableServicePort manageTableServicePort;
	public ManageTableResource(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
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
    public ResponseEntity<GetListItemsResponseDTO> getTables() {
        log.debug("Get all tables");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("--------Started Request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        GetListItemsResponseDTO getListItemsResponseDTO=manageTableServicePort.getTables(correlationid,ipaddress);
        
        if(getListItemsResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(getListItemsResponseDTO.getStatusCode()==200){
        	log.info("-----------Successfully Resopnse Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
        	return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
            
        }else{
        	log.info("-----------Failed Resopnse Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
        }
        
        
    }

    
    @GetMapping("/schema/{tableName}")
    @Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TableSchemaResponseDTO> getSchema(
    		@PathVariable String tableName) {
        log.debug("Get table schema");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("--------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {}, TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        TableSchemaResponseDTO tableSchemaResponseDTO=manageTableServicePort.getTableSchemaIfPresent(tableName,correlationid,ipaddress);
        
        if(tableSchemaResponseDTO == null)
        	throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if(tableSchemaResponseDTO.getStatusCode()==200){
        	log.info("-----------Successfully Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return ResponseEntity.status(HttpStatus.OK).body(tableSchemaResponseDTO);
        }else{
        	log.info("-----------Failed Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
        }
    }
	
	
    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponseDTO> createTable(
    		@RequestBody ManageTableDTO manageTableDTO) {
        log.debug("Create table");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("----------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        ApiResponseDTO apiResponseDTO=manageTableServicePort.createTableIfNotPresent(manageTableDTO,correlationid,ipaddress);
        if(apiResponseDTO.getResponseStatusCode()==200){
        	apiResponseDTO.setResponseMessage("Table: "+manageTableDTO.getTableName()+", is created successfully");
        	log.info("----------Successfully Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        }else{
        	log.debug("Table could not be created: {}", apiResponseDTO);
        	log.info("-----------Failed Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, "REST operation could not be performed");
        }
    }

	
    @DeleteMapping("/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponseDTO> deleteTable(@PathVariable String tableName) {
        log.debug("Delete table");
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("----------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",servicename,username,correlationid,ipaddress,timestamp,nameofCurrMethod);

        ApiResponseDTO apiResponseDTO=manageTableServicePort.deleteTable(tableName,correlationid,ipaddress);

        if(apiResponseDTO.getResponseStatusCode()==200){
        	log.info("-----------Successfully Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        }else{
        	log.debug("Exception occurred: {}", apiResponseDTO);
        	log.info("Failed Response Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
    
    
	@PutMapping("/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ApiResponseDTO> updateTableSchema(
			@PathVariable String tableName,
			@RequestBody TableSchemaDTO newTableSchemaDTO) {
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String correlationid = correlationID.generateUniqueCorrelationId();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(CorrelationID.CORRELATION_ID_HEADER_NAME, correlationid); 	
		String ipaddress=request.getRemoteAddr();
		String timestamp=utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("----------Started request of Service Name : {} , Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",servicename,username,correlationid,ipaddress,nameofCurrMethod);
		ApiResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO,correlationid,ipaddress);
		if(apiResponseDTO.getResponseStatusCode() == 200) {
			log.info("----------Successfully Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {},  Method name : {}",username,correlationid,nameofCurrMethod);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		}
		else {
			log.info("-----------Failed Resopnse Username : {}, Corrlation Id : {}, IP Address : {} , TimeStamp : {}, Method name : {}",username,correlationid,ipaddress,timestamp,nameofCurrMethod);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
}
