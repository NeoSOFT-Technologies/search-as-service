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
import com.searchservice.app.rest.errors.DeletionOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;
import com.searchservice.app.rest.errors.NullColumnOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.TableNotFoundException;
import com.searchservice.app.rest.errors.TableNotUnderDeletionException;

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
    @Operation(summary = "GET ALL THE CAPACITY PLANS AVAILABLE FOR TABLE CREATION.", security = @SecurityRequirement(name = "bearerAuth"))
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

    @GetMapping("/{tenantId}")
    @Operation(summary = "GET ALL THE TABLES FOR THE GIVEN TENANT ID.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> getTables(@PathVariable int tenantId) {

        log.debug("Get all tables");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        Response getListItemsResponseDTO = manageTableServicePort.getTables(tenantId, loggersDTO);

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

    @GetMapping("/{tenantId}/{tableName}")
    @Operation(summary = "GET SCHEMA OF A TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<TableSchemav2> getTable(@PathVariable int tenantId, @PathVariable String tableName) {
        log.debug("Get table info");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        if (tableDeleteServicePort.isTableUnderDeletion(tableName + "_" + tenantId)) {	
            throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), "Table " + tableName + " is Under Deletion Process");

        } else {

            // GET tableSchema
            TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getCurrentTableSchema(tenantId, tableName);

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

    @PostMapping("/{tenantId}")
    @Operation(summary = "CREATE A TABLE UNDER THE GIVEN TENANT ID.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> createTable(@PathVariable int tenantId, @RequestBody ManageTable manageTableDTO) {
        log.debug("Create table");
        if(null == manageTableDTO.getColumns() || manageTableDTO.getColumns().isEmpty())
        	throw new NullColumnOccurredException(HttpStatusCode.NULL_COLUMN.getCode(),HttpStatusCode.NULL_COLUMN.getMessage());
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);
        
        if(manageTableServicePort.checkIfTableNameisValid(manageTableDTO.getTableName())) {
        	 log.error("Table Name  {} is Invalid", manageTableDTO.getTableName());
             LoggerUtils.printlogger(loggersDTO, false, true);
             throw new InvalidInputOccurredException(HttpStatusCode.INVALID_TABLE_NAME.getCode(),"Creating Table Failed , as Invalid Table Name "+manageTableDTO.getTableName()+" is Provided");
        }else {
        	if(tableDeleteServicePort.isTableUnderDeletion(manageTableDTO.getTableName())) {
        		 throw new BadRequestOccurredException(400, "Table With Same Name "+manageTableDTO.getTableName()+" is Marked For Deletion");
               } 
        	  else {
        		  manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + tenantId);            	               	       
      	        Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO, loggersDTO);
      	        successMethod(nameofCurrMethod, loggersDTO);
      	        if (apiResponseDTO.getStatusCode() == 200) {
      	            LoggerUtils.printlogger(loggersDTO, false, false);
      	            apiResponseDTO.setMessage("Table-" + manageTableDTO.getTableName().split("_")[0] + ", is created successfully");
      	            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
      	        } else {
      	            log.info("Table could not be created: {}", apiResponseDTO);
      	            LoggerUtils.printlogger(loggersDTO, false, true);
      	            throw new BadRequestOccurredException(101, "REST operation could not be performed");
      	        }
        	   }
        	    }
        		
        	}

    @DeleteMapping("/{tenantId}/{tableName}")
    @Operation(summary = "DELETE A TABLE (SOFT DELETE).", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> deleteTable(@PathVariable int tenantId, @PathVariable String tableName) {
        log.debug("Delete table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        tableName = tableName + "_" + tenantId;
        if (!tableDeleteServicePort.isTableUnderDeletion(tableName.split("_")[0])) {
            successMethod(nameofCurrMethod, loggersDTO);
            if (tableDeleteServicePort.checkTableExistensce(tableName)) {

                Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(tenantId, tableName, loggersDTO);
                if (apiResponseDTO.getStatusCode() == 200) {
                    LoggerUtils.printlogger(loggersDTO, false, false);
                    return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
                } else {
                    log.debug("Exception occurred: {}", apiResponseDTO);
                    LoggerUtils.printlogger(loggersDTO, false, true);
                    throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
                }
            } else {
                throw new BadRequestOccurredException(400, "Table " + tableName.split("_")[0] + " For Tenant ID " + tenantId + " Does Not Exist");
            }
        } else {
            throw new BadRequestOccurredException(400, "Table " + tableName.split("_")[0] + " For Tenant ID " + tenantId + " is Already Under Deletion");
        }
    }

    @PutMapping("/restore/{tenantId}/{tableName}")
    @Operation(summary = "RESTORE A DELETED TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> undoTable(@PathVariable int tenantId, @PathVariable String tableName) {
        String tableNameForMessage = tableName;
        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = logGen(nameofCurrMethod, timestamp);

        tableName = tableName + "_" + tenantId;
        if(tableDeleteServicePort.isTableUnderDeletion(tableNameForMessage)) {
        Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName, loggersDTO);
        successMethod(nameofCurrMethod, loggersDTO);

        if (apiResponseDTO.getStatusCode() == 200) {
            LoggerUtils.printlogger(loggersDTO, false, false);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
        } else {
            LoggerUtils.printlogger(loggersDTO, false, true);
            log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
            throw new BadRequestOccurredException(400, "Error While Perforing Restoring for Table: "+tableNameForMessage);
        }
        }else {
        	throw new TableNotUnderDeletionException(HttpStatusCode.TABLE_NOT_UNDER_DELETION.getCode(),
        			"Table "+tableNameForMessage+" is Not Under Deletion");
        }
    }

    @PutMapping("/{tenantId}/{tableName}")
    @Operation(summary = "REPLACE SCHEMA OF AN EXISTING TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Response> updateTableSchema(
            @PathVariable int tenantId, @PathVariable String tableName, @RequestBody TableSchema newTableSchemaDTO) {
        log.debug("Search schema update");
        log.debug("Received Schema as in Request Body: {}", newTableSchemaDTO);

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
        String timestamp = LoggerUtils.utcTime().toString();
        LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username, nameofCurrMethod, timestamp);
        LoggerUtils.printlogger(loggersDTO, true, false);
        loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
        loggersDTO.setIpaddress(loggersDTO.getIpaddress());
        tableName = tableName + "_" + tenantId;
        if(!manageTableServicePort.isTableExists(tableName)) {
        	throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),"Table "+tableName.split("_")[0]+ 
        			" having TenantID: "+tableName.split("_")[1]+" Not Found");
        }else {
        if (!tableDeleteServicePort.isTableUnderDeletion(tableName.split("_")[0])) {
            newTableSchemaDTO.setTableName(tableName);

            Response apiResponseDTO = manageTableServicePort.updateTableSchema(
            		tenantId, tableName.split("_")[0], newTableSchemaDTO, loggersDTO);

            successMethod(nameofCurrMethod, loggersDTO);

            if (apiResponseDTO.getStatusCode() == 200) {
                LoggerUtils.printlogger(loggersDTO, false, false);
                apiResponseDTO.setMessage("Table is updated successfully");
                return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
            } else {
                LoggerUtils.printlogger(loggersDTO, false, true);
                throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
            }
        } else {
            throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(), "Table " + tableName + " is Under Deletion Process");
        }
        }
    }
}
