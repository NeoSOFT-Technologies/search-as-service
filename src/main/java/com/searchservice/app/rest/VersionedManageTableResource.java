package com.searchservice.app.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.DeletionOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

//@RestController
//@RequestMapping("${base-url.api-endpoint.versioned-home}" + "/manage/table")
public class VersionedManageTableResource {
	private String servicename = "Versioned_Manage_Table_Resource";

	private String username = "Username";

	private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

	private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;
	@Autowired
	ManageTableServicePort manageTableServicePort;

	@Autowired
	TableDeleteServicePort tableDeleteServicePort;

	@Autowired
	LoggerUtils loggerUtils;

	private List<Object> listOfParameters;
	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort,
			TableDeleteServicePort tableDeleteServicePort, LoggerUtils loggerUtils) {
		this.manageTableServicePort = manageTableServicePort;
		this.tableDeleteServicePort = tableDeleteServicePort;
		this.loggerUtils = loggerUtils;
	}

	private void successMethod(String nameofCurrMethod, LoggersDTO loggersDTO) {
		String timestamp;
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = loggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
	}

	@GetMapping("/capacity-plans")
	@Operation(summary = "/get-capacity-plans")
	public GetCapacityPlan capacityPlans() {
		
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		GetCapacityPlan getCapacityPlanDTO = manageTableServicePort.capacityPlans(loggersDTO);

		successMethod(nameofCurrMethod, loggersDTO);
		loggerUtils.printlogger(loggersDTO, false, false);
		return getCapacityPlanDTO;
	}

	@GetMapping("/{tenantId}")
	@Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
	public Response getTables(@PathVariable int tenantId) {
		log.debug("Get all tables");
		listOfParameters = new ArrayList<Object>();
        listOfParameters.add(tenantId);
        
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		Response getListItemsResponseDTO = manageTableServicePort.getTables(tenantId, loggersDTO);

		successMethod(nameofCurrMethod, loggersDTO);

		if (getListItemsResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (getListItemsResponseDTO.getStatusCode() == 200) {
			loggerUtils.printlogger(loggersDTO, false, false);
			return getListItemsResponseDTO;
		} else {
			loggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}

	@GetMapping("/{tenantId}/{tableName}")
	@Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
	public TableSchemav2 getTable(@PathVariable int tenantId, @PathVariable String tableName) {
		log.debug("Get table info");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp,
				listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		tableName = tableName + "_" + tenantId;
		if (tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			throw new BadRequestOccurredException(400, "Table " + tableName + " is Under Deletion Process");
		} else {
			// GET tableSchema
			TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName, loggersDTO);

			successMethod(nameofCurrMethod, loggersDTO);

			if (tableInfoResponseDTO == null)
				throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);

			if (tableInfoResponseDTO.getStatusCode() == 200) {
				tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
				loggerUtils.printlogger(loggersDTO, false, false);
				return tableInfoResponseDTO;
			} else {
				loggerUtils.printlogger(loggersDTO, false, true);
				throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
			}
		}
	}

	@PostMapping
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public Response createTable(@RequestBody ManageTable manageTableDTO) {
		

		listOfParameters = new ArrayList<Object>();
        listOfParameters.add(manageTableDTO);
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);

		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO, loggersDTO);

		successMethod(nameofCurrMethod, loggersDTO);

		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO.setMessage("Table: " + manageTableDTO.getTableName() + ", is created successfully");
			loggerUtils.printlogger(loggersDTO, false, false);
			return apiResponseDTO;
		} else {
			log.debug("Table could not be created: {}", apiResponseDTO);
			loggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}

	@DeleteMapping("/{tenantId}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public Response deleteTable(@PathVariable String tableName, @PathVariable int tenantId) {
		listOfParameters = new ArrayList<Object>();
        listOfParameters.add(tenantId);
        listOfParameters.add(tableName);


		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		String tableNameForMessage = tableName;

		tableName = tableName + "_" + tenantId;
		if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			successMethod(nameofCurrMethod, loggersDTO);
			if (tableDeleteServicePort.checkTableExistensce(tableName)) {
				Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(tenantId, tableName, loggersDTO);
				if (apiResponseDTO.getStatusCode() == 200) {
					loggerUtils.printlogger(loggersDTO, false, false);
					return apiResponseDTO;
				} else {
					log.debug("Exception occurred: {}", apiResponseDTO);
					loggerUtils.printlogger(loggersDTO, false, true);
					throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
				}
			} else {
				throw new BadRequestOccurredException(400,
						"Table " + tableNameForMessage + " For Client ID " + tenantId + " Does Not Exist");
			}
		} else {
			throw new BadRequestOccurredException(400,
					"Table " + tableNameForMessage + " For Client ID " + tenantId + " is Already Under Deletion");
		}
	}
	@PutMapping("/restore/{tenantId}/{tableName}")
	@Operation(summary = "/restore-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> undoTable(@PathVariable String tableName, @PathVariable int tenantId) {
		listOfParameters = new ArrayList<Object>();
        listOfParameters.add(tenantId);
        listOfParameters.add(tableName);
		String tableNameForMessage = tableName;
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName, loggersDTO);

		successMethod(nameofCurrMethod, loggersDTO);

		if (apiResponseDTO.getStatusCode() == 200) {
			loggerUtils.printlogger(loggersDTO, false, false);
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			loggerUtils.printlogger(loggersDTO, false, true);
			log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
			throw new BadRequestOccurredException(400, tableNameForMessage + " is not available for restoring");
		}
	}


	@PutMapping("/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public Response updateTableSchema(@PathVariable String tableName, @RequestBody TableSchema newTableSchemaDTO) {
		listOfParameters = new ArrayList<Object>();
        listOfParameters.add(tableName);
        listOfParameters.add(newTableSchemaDTO);
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = loggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = loggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp, listOfParameters);
		loggerUtils.printlogger(loggersDTO, true, false);

		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			newTableSchemaDTO.setTableName(tableName);
			Response apiResponseDTO = manageTableServicePort.updateTableSchema(101, tableName, newTableSchemaDTO,
					loggersDTO);
			successMethod(nameofCurrMethod, loggersDTO);

			if (apiResponseDTO.getStatusCode() == 200) {
				loggerUtils.printlogger(loggersDTO, false, false);
				return apiResponseDTO;
			} else {
				loggerUtils.printlogger(loggersDTO, false, true);
				throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
			}
		} else {
			throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), "Table " + tableName + " is Under Deletion Process");
		}
	}
}
