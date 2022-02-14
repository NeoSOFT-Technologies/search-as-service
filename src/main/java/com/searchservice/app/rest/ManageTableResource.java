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
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}" + "/manage/table")
public class ManageTableResource {

	private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

	private static final String BAD_REQUEST_MSG = "REST call could not be performed";

	private String servicename = "Manage_Table_Resource";

	private String username = "Username";

	private ManageTableServicePort manageTableServicePort;

	private TableDeleteServicePort tableDeleteServicePort;

	public ManageTableResource(ManageTableServicePort manageTableServicePort,
			TableDeleteServicePort tableDeleteServicePort) {
		this.manageTableServicePort = manageTableServicePort;
		this.tableDeleteServicePort = tableDeleteServicePort;
	}

	@GetMapping("/capacity-plans")
	@Operation(summary = "/get-capacity-plans")
	public ResponseEntity<GetCapacityPlanDTO> capacityPlans() {
		log.debug("Get capacity plans");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		GetCapacityPlanDTO getCapacityPlanDTO = manageTableServicePort.capacityPlans(loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		LoggerUtils.printlogger(loggersDTO, false, false);
		return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);
	}

	@GetMapping
	@Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> getTables() {
		log.debug("Get all tables");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		ResponseDTO getListItemsResponseDTO = manageTableServicePort.getTables(loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		if (getListItemsResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (getListItemsResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}

	@GetMapping("/details/{tableName}")
	@Operation(summary = "/ Get the table details like Shards, Nodes & Replication Factor.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Map> getTableDetails(@PathVariable String tableName) {

		log.debug("getCollectionDetails");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		Map<?, ?> responseMap = manageTableServicePort.getTableDetails(tableName, loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		if (!responseMap.containsKey("Error")) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(responseMap);
		} else {
			LoggerUtils.printlogger(loggersDTO, false, true);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
		}

	}

	@GetMapping("/schema/{clientid}/{tableName}")
	@Operation(summary = "/get-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<TableSchemaDTO> getSchema(@PathVariable String tableName, @PathVariable int clientid) {
		log.debug("Get table schema");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		tableName = tableName + "_" + clientid;
		TableSchemaDTO tableSchemaResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName, loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		if (tableSchemaResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (tableSchemaResponseDTO.getStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(tableSchemaResponseDTO);
		} else {
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
		}
	}

	@PostMapping("/{clientid}")
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> createTable(@PathVariable int clientid,
			@RequestBody ManageTableDTO manageTableDTO) {
		log.debug("Create table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + clientid);
		ResponseDTO apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO, loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			apiResponseDTO.setResponseMessage("Table: " + manageTableDTO.getTableName() + ", is created successfully");
			LoggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.debug("Table could not be created: {}", apiResponseDTO);
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, "REST operation could not be performed");
		}
	}

	@DeleteMapping("/{clientid}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> deleteTable(@PathVariable String tableName, @PathVariable int clientid) {
		log.debug("Delete table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		tableName = tableName + "_" + clientid;
		if (tableDeleteServicePort.checkTableExistensce(tableName)) {
			ResponseDTO apiResponseDTOs = tableDeleteServicePort.initializeTableDelete(clientid, tableName,loggersDTO);
			if (apiResponseDTOs.getResponseStatusCode() == 200) {
				LoggerUtils.printlogger(loggersDTO, false, false);
				return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTOs);
			} else {
				log.debug("Exception occurred: {}", apiResponseDTOs);
				LoggerUtils.printlogger(loggersDTO, false, true);
				throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
			}
		} else {
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400,
					"Table " + tableName + " For Client ID " + clientid + " Does Not Exist");
		}
	}

	@PutMapping("/{clientId}")
	@Operation(summary = "/undo-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<ResponseDTO> undoTable(@PathVariable int clientId) {
		log.debug("Undo Table Delete");
		ResponseDTO apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(clientId);
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.debug("Exception Occured While Performing Undo Delete For Client ID: {} ", clientId);
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
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
		LoggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		newTableSchemaDTO.setTableName(tableName);
		ResponseDTO apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO, loggersDTO);

		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}
}
