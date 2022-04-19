package com.searchservice.app.rest;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.DeletionOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.TableNotFoundException;
import com.searchservice.app.rest.errors.TableNotUnderDeletionException;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}" + "/manage/table")
public class ManageTableResource {

	private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

	private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;
	private static final String TABLE = "Table ";
	private static final String TENANTID = " For TenantID: ";
	private static final String MSG_SEPERATOR = " is ";

         @Autowired
	private ManageTableServicePort manageTableServicePort;
    @Autowired
    private TableDeleteServicePort tableDeleteServicePort;

    public ManageTableResource(ManageTableServicePort manageTableServicePort, TableDeleteServicePort tableDeleteServicePort) {
        this.manageTableServicePort = manageTableServicePort;
        this.tableDeleteServicePort = tableDeleteServicePort;
    }
	
	@GetMapping("/capacity-plans")
	@Operation(summary = "GET ALL THE CAPACITY PLANS AVAILABLE FOR TABLE CREATION.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<CapacityPlanResponse> capacityPlans() {
		log.debug("Get capacity plans");

		return ResponseEntity.status(HttpStatus.OK).body(manageTableServicePort.capacityPlans());
	}

	@GetMapping("/")
	@Operation(summary = "GET ALL THE TABLES FOR THE GIVEN TENANT ID.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> getTables(@RequestParam int tenantId) {

		Response getListItemsResponseDTO = manageTableServicePort.getTables(tenantId);

		if (getListItemsResponseDTO == null)
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		if (getListItemsResponseDTO.getStatusCode() == 200) {

			List<String> existingTablesList = getListItemsResponseDTO.getData();
			existingTablesList.removeAll(tableDeleteServicePort.getTableUnderDeletion());
			getListItemsResponseDTO.setData(existingTablesList);
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {

			throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), 
					ResponseMessages.DEFAULT_EXCEPTION_MSG);
		}
	}

	@GetMapping("/{tableName}")
	@Operation(summary = "GET SCHEMA OF A TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<TableSchemav2> getTable(@RequestParam int tenantId, @PathVariable String tableName) {

		if (tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					TABLE + tableName + TENANTID+ tenantId+ MSG_SEPERATOR + HttpStatusCode.UNDER_DELETION_PROCESS.getMessage());

		} else {

			// GET tableSchema
			TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getCurrentTableSchema(tenantId, tableName);

			if (tableInfoResponseDTO == null)
				throw new NullPointerOccurredException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(),
						HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());

			if (tableInfoResponseDTO.getStatusCode() == 200) {

				tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
				return ResponseEntity.status(HttpStatus.OK).body(tableInfoResponseDTO);
			} else {
				throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode()
						, "Something Went Wrong While Fetching Schema Details For Table: "+tableName +TENANTID + tenantId);
			}
		}
	}

	@PostMapping("/")
	@Operation(summary = "CREATE A TABLE UNDER THE GIVEN TENANT ID.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> createTable(@RequestParam int tenantId, @RequestBody ManageTable manageTableDTO) {

		if (manageTableServicePort.checkIfTableNameisValid(manageTableDTO.getTableName())) {
			log.error("Table Name  {} is Invalid", manageTableDTO.getTableName());

			throw new InvalidInputOccurredException(HttpStatusCode.INVALID_TABLE_NAME.getCode(),
					"Creating Table Failed , as Invalid Table Name " + manageTableDTO.getTableName() + " is Provided");
		} else {
			if (tableDeleteServicePort.isTableUnderDeletion(manageTableDTO.getTableName())) {
				throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
						"Table With Same Name " + manageTableDTO.getTableName() + MSG_SEPERATOR + HttpStatusCode.UNDER_DELETION_PROCESS.getMessage());
			} else {
				manageTableDTO.setTableName(manageTableDTO.getTableName() + "_" + tenantId);
				Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO);
 
				if (apiResponseDTO.getStatusCode() == 200) {
					apiResponseDTO.setMessage(
							"Table-" + manageTableDTO.getTableName().split("_")[0] + ", is created successfully");
					return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
				} else {
					log.info(TABLE +"could not be created: {}", apiResponseDTO);
					throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
							"Something Went Wrong While Creating Table "+manageTableDTO.getTableName().split("_")[0]+ TENANTID + tenantId);
				}
			}
		}

	}
	@DeleteMapping("/{tableName}")
	@Operation(summary = "DELETE A TABLE (SOFT DELETE).", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> deleteTable(@RequestParam int tenantId, @PathVariable String tableName) {
		tableName = tableName + "_" + tenantId;
		if (!tableDeleteServicePort.isTableUnderDeletion(tableName.split("_")[0])) {

			if (tableDeleteServicePort.checkTableExistensce(tableName)) {

				Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(tenantId, tableName);
				if (apiResponseDTO.getStatusCode() == 200) {

					return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
				} else {
					log.debug("Exception occurred: {}", apiResponseDTO);

					throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), BAD_REQUEST_MSG);
				}
			} else {
				throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),
						TABLE + tableName.split("_")[0] + TENANTID + tenantId + " "+ HttpStatusCode.TABLE_NOT_FOUND.getMessage());
			}
		} else {
			throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					TABLE + tableName.split("_")[0] + TENANTID + tenantId + MSG_SEPERATOR + HttpStatusCode.UNDER_DELETION_PROCESS.getMessage());
		}
	}


	@PutMapping("/restore/{tableName}")
	@Operation(summary = "RESTORE A DELETED TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> restoreTable(@RequestParam int tenantId, @PathVariable String tableName) {
		String tableNameForMessage = tableName;

		tableName = tableName + "_" + tenantId;
		 if(tableDeleteServicePort.isTableUnderDeletion(tableNameForMessage)) {
		Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName);

		if (apiResponseDTO.getStatusCode() == 200) {

			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {

			log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
			throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					"Something Went Wrong While Performing Restore for "+TABLE+ tableNameForMessage);
		}}else {
        	throw new TableNotUnderDeletionException(HttpStatusCode.TABLE_NOT_UNDER_DELETION.getCode(),
        			TABLE+tableNameForMessage+TENANTID + tenantId + MSG_SEPERATOR + HttpStatusCode.TABLE_NOT_UNDER_DELETION.getMessage() );
        }
	}

	@PutMapping("/{tableName}")
	@Operation(summary = "REPLACE SCHEMA OF AN EXISTING TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
	public ResponseEntity<Response> updateTableSchema(@RequestParam int tenantId, @PathVariable String tableName,
			@RequestBody TableSchema newTableSchemaDTO) {

		tableName = tableName + "_" + tenantId;
		if(!manageTableServicePort.isTableExists(tableName)) {
        	throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),
        			TABLE + tableName.split("_")[0] + TENANTID + tenantId + " "+ HttpStatusCode.TABLE_NOT_FOUND.getMessage());
        }else {
		if (!tableDeleteServicePort.isTableUnderDeletion(tableName.split("_")[0])) {
			newTableSchemaDTO.setTableName(tableName);

			Response apiResponseDTO = manageTableServicePort.updateTableSchema(tenantId, tableName.split("_")[0],
					newTableSchemaDTO);

			if (apiResponseDTO.getStatusCode() == 200) {

				apiResponseDTO.setMessage("Table is updated successfully");
				return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
			} else {

				throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), BAD_REQUEST_MSG);
			}
		} else {
			throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					TABLE + tableName.split("_")[0] + TENANTID + tenantId + MSG_SEPERATOR + HttpStatusCode.UNDER_DELETION_PROCESS.getMessage());
		}
        }
	}

	
}
