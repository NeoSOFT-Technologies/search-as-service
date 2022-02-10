package com.searchservice.app.rest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.logger.CorrelationID;
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
@RequestMapping("/api/v2/manage/table")
public class ManageTableResource {

	private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

	private static final String BAD_REQUEST_MSG = "REST call could not be performed";
	private static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";
	
	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";

	private ManageTableServicePort manageTableServicePort;
	
	ManageTableResource manageTableResource;
	

	public ManageTableResource(ManageTableServicePort manageTableServicePort) {
		this.manageTableServicePort = manageTableServicePort;
	}

	@GetMapping("/capacity-plans")
	@Operation(summary = "/get-capacity-plans")
	public ResponseEntity<GetCapacityPlanDTO> capacityPlans() {
		log.debug("Get capacity plans");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		GetCapacityPlanDTO getCapacityPlanDTO = manageTableServicePort.capacityPlans(loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		LoggerUtils.Printlogger(loggersDTO,false,false);
		return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);
	}

	@GetMapping
	@Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> getTables() {
		log.debug("Get all tables");
		
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
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
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {
			LoggerUtils.Printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}

	@GetMapping("/details/{tableName}")
	@Operation(summary = "/ Get the table details like Shards, Nodes & Replication Factor.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Map> getTableDetails(@PathVariable String tableName) {

		log.debug("getCollectionDetails");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		Map responseMap = manageTableServicePort.getTableDetails(tableName,loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (!responseMap.containsKey("Error")) {
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(responseMap);
		} else {
			LoggerUtils.Printlogger(loggersDTO,false,true);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
		}

	}

	@GetMapping("/schema/{clientid}/{tableName}")
	@Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<TableSchemaDTO> getSchema(@PathVariable String tableName, @PathVariable int clientid) {
		log.debug("Get table schema");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		tableName = tableName + "_" + clientid;
		TableSchemaDTO tableSchemaResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName,loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (tableSchemaResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (tableSchemaResponseDTO.getStatusCode() == 200) {
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(tableSchemaResponseDTO);
		} else {
			LoggerUtils.Printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}

	@PostMapping("/{clientid}")
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> createTable(@PathVariable int clientid,
			@RequestBody ManageTableDTO manageTableDTO) {
		log.debug("Create table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		ResponseDTO apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO,loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			apiResponseDTO.setResponseMessage("Table: " + manageTableDTO.getTableName() + ", is created successfully");
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.debug("Table could not be created: {}", apiResponseDTO);
			LoggerUtils.Printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, "REST operation could not be performed");
		}
	}

	@DeleteMapping("/{clientid}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> deleteTable(@PathVariable String tableName, @PathVariable int clientid) {
		log.debug("Delete table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		tableName = tableName + "_" + clientid;
		ResponseDTO apiResponseDTO = manageTableServicePort.deleteTable(tableName,loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.debug("Exception occurred: {}", apiResponseDTO);
			LoggerUtils.Printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}

	@PutMapping("/{clientid}/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> updateTableSchema(@PathVariable String tableName, @PathVariable int clientid,
			@RequestBody TableSchemaDTO newTableSchemaDTO) {
		tableName = tableName + "_" + clientid;
		log.debug("Solr schema update");
		log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.Printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
		newTableSchemaDTO.setTableName(newTableSchemaDTO.getTableName() + "_" + clientid);
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO,loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.Printlogger(loggersDTO,false,false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		}
		else {
			LoggerUtils.Printlogger(loggersDTO,false,true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
}
