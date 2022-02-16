package com.searchservice.app.rest;

import java.util.List;
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
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}" + "/manage/table")
public class ManageTableResource {
    private String servicename = "Manage_Table_Resource";

    private String username = "Username";

    private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

    private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;

    private ManageTableServicePort manageTableServicePort;

    private TableDeleteServicePort tableDeleteServicePort;

    public ManageTableResource(ManageTableServicePort manageTableServicePort, TableDeleteServicePort tableDeleteServicePort) {
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
    public ResponseEntity<GetCapacityPlan> capacityPlans() {
        log.debug("Get capacity plans");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        GetCapacityPlan getCapacityPlanDTO = manageTableServicePort.capacityPlans(loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
        LoggerUtils.printlogger(loggersDTO, false, false);
        return ResponseEntity.status(HttpStatus.OK).body(getCapacityPlanDTO);
    }

    private LoggersDTO logGen(String nameofCurrMethod, String timestamp) {
        LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
        LoggerUtils.printlogger(loggersDTO, true, false);
        loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
        loggersDTO.setIpaddress(loggersDTO.getIpaddress());
        return loggersDTO;
    }

    @GetMapping("/{clientId}")
    @Operation(summary = "/all-tables summary", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> getTables(@PathVariable int clientId) {

        log.debug("Get all tables");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        Response getListItemsResponseDTO = manageTableServicePort.getTables(clientId, loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);

        if (getListItemsResponseDTO == null)
            throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
        if (getListItemsResponseDTO.getStatusCode() == 200) {
            LoggerUtils.printlogger(loggersDTO, false, false);
            List<String> existingTablesList = getListItemsResponseDTO.getData();
            existingTablesList.removeAll(tableDeleteServicePort.getTableUnderDeletion());
            getListItemsResponseDTO.setData(existingTablesList);
            return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
        } else {
            LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, ResponseMessages.DEFAULT_EXCEPTION_MSG);
        }
    }

    @GetMapping("/{clientId}/{tableName}")
    @Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TableSchemav2> getTable(@PathVariable int clientId, @PathVariable String tableName) {
        log.debug("Get table info");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        if (tableDeleteServicePort.isTableUnderDeletion(tableName + "_" + clientId)) {
            throw new BadRequestOccurredException(400, "Table " + tableName + " is Under Deletion Process");
        } else {

            // GET tableSchema
            TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getCurrentTableSchema(clientId, tableName);

            successMethod(nameofCurrMethod, loggersDTO);

            if (tableInfoResponseDTO == null)
                throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);

            if (tableInfoResponseDTO.getStatusCode() == 200) {
                LoggerUtils.printlogger(loggersDTO, false, false);
                tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
                return ResponseEntity.status(HttpStatus.OK).body(tableInfoResponseDTO);
            } else {
                LoggerUtils.printlogger(loggersDTO, false, true);
                throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
            }
        }
    }

    @PostMapping("/{clientId}")
    @Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> createTable(@PathVariable int clientId, @RequestBody ManageTable manageTableDTO) {
        log.debug("Create table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + clientId);

        Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO, loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);

        if (apiResponseDTO.getStatusCode() == 200) {
            LoggerUtils.printlogger(loggersDTO, false, false);
            apiResponseDTO.setMessage("Table- " + manageTableDTO.getTableName().split("_")[0] + ", is created successfully");
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } else {
            log.info("Table could not be created: {}", apiResponseDTO);
            LoggerUtils.printlogger(loggersDTO, false, true);
            throw new BadRequestOccurredException(400, "REST operation could not be performed");
        }
    }

    @DeleteMapping("/{clientId}/{tableName}")
    @Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> deleteTable(@PathVariable String tableName, @PathVariable int clientId) {
        log.debug("Delete table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        tableName = tableName + "_" + clientId;
        if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {
            successMethod(nameofCurrMethod, loggersDTO);
            if (tableDeleteServicePort.checkTableExistensce(tableName)) {

                Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(clientId, tableName, loggersDTO);
                if (apiResponseDTO.getStatusCode() == 200) {
                    LoggerUtils.printlogger(loggersDTO, false, false);
                    return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
                } else {
                    log.debug("Exception occurred: {}", apiResponseDTO);
                    LoggerUtils.printlogger(loggersDTO, false, true);
                    throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
                }
            } else {
                throw new BadRequestOccurredException(400, "Table " + tableName.split("_")[0] + " For Client ID " + clientId + " Does Not Exist");
            }
        } else {
            throw new BadRequestOccurredException(400, "Table " + tableName.split("_")[0] + " For Client ID " + clientId + " is Already Under Deletion");
        }
    }

    @PutMapping("/restore/{clientid}/{tableName}")
    @Operation(summary = "/restore-table-delete", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> undoTable(@PathVariable String tableName, @PathVariable int clientid) {
        String tableNameForMessage = tableName;
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        tableName = tableName + "_" + clientid;
        Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName, loggersDTO);
        successMethod(nameofCurrMethod, loggersDTO);

        if (apiResponseDTO.getStatusCode() == 200) {
            LoggerUtils.printlogger(loggersDTO, false, false);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } else {
            LoggerUtils.printlogger(loggersDTO, false, true);
            log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
            throw new BadRequestOccurredException(400, tableNameForMessage + " is not available for restoring");
        }
    }

    @PutMapping("/{clientId}/{tableName}")
    @Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> updateTableSchema(@PathVariable String tableName, @PathVariable int clientId, @RequestBody TableSchema newTableSchemaDTO) {
        log.debug("Solr schema update");
        log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
        LoggerUtils.printlogger(loggersDTO, true, false);
        loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
        loggersDTO.setIpaddress(loggersDTO.getIpaddress());
        tableName = tableName + "_" + clientId;

        if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {
            newTableSchemaDTO.setTableName(tableName);

            Response apiResponseDTO = manageTableServicePort.updateTableSchema(clientId, tableName, newTableSchemaDTO, loggersDTO);

            successMethod(nameofCurrMethod, loggersDTO);

            if (apiResponseDTO.getStatusCode() == 200) {
                LoggerUtils.printlogger(loggersDTO, false, false);
                return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
            } else {
                LoggerUtils.printlogger(loggersDTO, false, true);
                throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
            }
        } else {
            throw new BadRequestOccurredException(400, "Table " + tableName + " is Under Deletion Process");
        }
    }
}
