package com.searchservice.app.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.versioned-home}" + "/manage/table")
public class VersionedManageTableResource {
	private String servicename = "Versioned_Manage_Table_Resource";

	private String username = "Username";

    private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

    private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;

    private ManageTableServicePort manageTableServicePort;

    private TableDeleteServicePort tableDeleteServicePort;

    public VersionedManageTableResource(ManageTableServicePort manageTableServicePort, TableDeleteServicePort tableDeleteServicePort) {
        this.manageTableServicePort = manageTableServicePort;
        this.tableDeleteServicePort = tableDeleteServicePort;
    }
    private void successMethod(String nameofCurrMethod, LoggersDTO loggersDTO) {
		String timestamp;
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
	}
    @GetMapping("/capacity-plans")
    @Operation(summary = "/get-capacity-plans")
    public GetCapacityPlan capacityPlans() {
        log.debug("Get capacity plans");
        
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        GetCapacityPlan getCapacityPlanDTO = manageTableServicePort.capacityPlans(loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        LoggerUtils.printlogger(loggersDTO, false, false);
        return getCapacityPlanDTO;
    }

    @GetMapping("/{clientid}")
    @Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
    public Response getTables(@PathVariable int clientId) {
        log.debug("Get all tables");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        Response getListItemsResponseDTO = manageTableServicePort.getTables(clientId,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        
        if (getListItemsResponseDTO == null)
            throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
        if (getListItemsResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO, false, false);
            return getListItemsResponseDTO;
        } else {
        	LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    @GetMapping("/{clientid}/{tableName}")
    @Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
    public TableSchemav2 getTable(@PathVariable int clientid, @PathVariable String tableName) {
        log.debug("Get table info");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName + "_" + clientid;

        // GET tableDetails
        Map<Object, Object> tableDetailsMap = manageTableServicePort.getTableDetails(tableName,loggersDTO);

        // GET tableSchema
        TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName,loggersDTO);
        if (tableInfoResponseDTO == null)
            throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);

        // SET tableDetails in tableInfoResponseDTO

        successMethod(nameofCurrMethod, loggersDTO);
        
        tableInfoResponseDTO.setTableDetails(tableDetailsMap);
        if (tableInfoResponseDTO.getStatusCode() == 200) {
            tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
            LoggerUtils.printlogger(loggersDTO, false, false);
            return tableInfoResponseDTO;
        } else {
        	LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
        }
    }

    @PostMapping
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public Response createTable(@RequestBody ManageTable manageTableDTO) {
        log.debug("Create table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        
        if (apiResponseDTO.getStatusCode() == 200) {
            apiResponseDTO.setMessage("Table: " + manageTableDTO.getTableName() + ", is created successfully");
            LoggerUtils.printlogger(loggersDTO, false, false);
            return apiResponseDTO;
        } else {
            log.debug("Table could not be created: {}", apiResponseDTO);
            LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    @DeleteMapping("/{clientid}/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public Response deleteTable(@PathVariable String tableName, @PathVariable int clientid) {
        log.debug("Delete table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName + "_" + clientid;

        successMethod(nameofCurrMethod, loggersDTO);
        
        if (tableDeleteServicePort.checkTableExistensce(tableName)) {
            Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(clientid, tableName,loggersDTO);
            if (apiResponseDTO.getStatusCode() == 200) {
            	LoggerUtils.printlogger(loggersDTO, false, false);
                return apiResponseDTO;
            } else {
                log.debug("Exception occurred: {}", apiResponseDTO);
                LoggerUtils.printlogger(loggersDTO, false, true);
                throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
            }
        } else {
        	LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, "Table " + tableName + " For Client ID " + clientid + " Does Not Exist");
        }
    }

    @PutMapping("/{clientId}")
    @Operation(summary = "/undo-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
    public Response undoTable(@PathVariable int clientId) {
        log.debug("Undo Table Delete");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(clientId,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        
        if (apiResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO, false, false);
            return apiResponseDTO;
        } else {
            log.debug("Exception Occured While Performing Undo Delete For Client ID: {} ", clientId);
            LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }

    @PutMapping("/{tableName}")
    @Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public Response updateTableSchema(@PathVariable String tableName, @RequestBody TableSchema newTableSchemaDTO) {
        log.debug("Solr schema update");
        log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        Response apiResponseDTO = manageTableServicePort.updateTableSchema(tableName, newTableSchemaDTO,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        
        if (apiResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO, false, false);
            return apiResponseDTO;
        }
        else {
        	LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
        }
    }
}
