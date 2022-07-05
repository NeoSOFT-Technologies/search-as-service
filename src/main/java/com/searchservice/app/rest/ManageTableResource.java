package com.searchservice.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.CreateTable;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}" + "/manage/table")
public class ManageTableResource {

	private final Logger log = LoggerFactory.getLogger(ManageTableResource.class);

	private static final String BAD_REQUEST_MSG = HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage();

	private static final String TABLE = "Table ";
	private static final String ERROR_MSG ="Something Went Wrong While";

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
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<CapacityPlanResponse> capacityPlans() {
		log.debug("Get capacity plans");

		return ResponseEntity.status(HttpStatus.OK).body(manageTableServicePort.capacityPlans());
	}

	@GetMapping("/")
	@Operation(summary = "GET ALL THE TABLES FOR THE GIVEN TENANT.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<Response> getTablesByTenantName(@RequestParam String tenantName) {

		Response getListItemsResponseDTO = manageTableServicePort.getTablesForTenant(tenantName);
		if (getListItemsResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getListItemsResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, String.format(ERROR_MSG+ "Fetching Tables Having TenantID; %d",tenantName));
		}
	}
	
	@GetMapping("/tables-list")
	@Operation(summary = "GET TABLES FOR THE GIVEN TENANT ALONG WITH PAGINATION", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<Response> getTablesByTenantNamePagination(@RequestParam String tenantName, @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize) {

		Response getListItemsResponseDTO = manageTableServicePort.getTablesForTenantPagination(
				tenantName, pageNumber, pageSize);
		if (getListItemsResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getListItemsResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, String.format(ERROR_MSG+ "Fetching List of Tables Having TenantID; %d",tenantName));
		}
	}
	
	@GetMapping("/all-tables")
	@Operation(summary = "GET ALL THE TABLES FROM THE SERVER.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<Response> getALLTables(@RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize) {

		Response getListItemsResponseDTO = manageTableServicePort.getAllTables(pageNumber, pageSize);
		if (getListItemsResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getListItemsResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(getListItemsResponseDTO);
		} else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, String.format(ERROR_MSG+ " Fetching All Tables From The Server"));
		}
	}
	
	@GetMapping("/deletion/all-tables")
	@Operation(summary = "GET ALL THE TABLES UNDER DELETION.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<Response> getALLTablesUnderDeletion(@RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize) {

		Response getDeleteTableListResponseDTO = tableDeleteServicePort.getTablesUnderDeletionPagination(true, pageNumber, pageSize);
		if (getDeleteTableListResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getDeleteTableListResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(getDeleteTableListResponseDTO);
		} else {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, String.format(ERROR_MSG+ " Fetching All Tables Under Deletion"));
		}
	}
	
	@GetMapping("/deletion")
	@Operation(summary = "GET ALL THE TABLES UNDER DELETION FOR GIVEN TENANT", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<Response> getALLTablesUnderDeletionByTenantName(
			@RequestParam String tenantName, @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "6") int pageSize) {

		Response getDeleteTableListResponseDTO = tableDeleteServicePort.getTablesUnderDeletionForTenant(
				tenantName, pageNumber, pageSize);
		if (getDeleteTableListResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (getDeleteTableListResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(getDeleteTableListResponseDTO);
			
		} else {
			throw new CustomException(
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, 
					String.format(ERROR_MSG+ " Fetching All Tables Under Deletion For Tenant: {}", tenantName));
		}
	}

	@GetMapping("/{tableName}")
	@Operation(summary = "GET TABLE DETAILS (INCLUDING SCHEMA).", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isViewPermissionEnabled()")
	public ResponseEntity<TableSchema> getTable(@RequestParam int tenantId, @PathVariable String tableName) {
			// GET tableSchema
		TableSchema tableInfoResponseDTO = manageTableServicePort.getCurrentTableSchema(tenantId, tableName);
		if (tableInfoResponseDTO == null)
			throw new CustomException(
					HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(),
					HttpStatusCode.NULL_POINTER_EXCEPTION, 
					"Table Information could not be retrieved. "+HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		if (tableInfoResponseDTO.getStatusCode() == 200) {
			tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(tableInfoResponseDTO);
		} else {
			throw new CustomException(
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION, 
					String.format(ERROR_MSG+ " Fetching Schema Details For Table: %s Having TenantID; %d", tableName, tenantId));
		}
	}

	@PostMapping("/")
	@Operation(summary = "CREATE A TABLE UNDER THE GIVEN TENANT ID.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isCreatePermissionEnabled()")
	public ResponseEntity<Response> createTable(@RequestParam int tenantId, @RequestBody CreateTable createTableDTO) {
			createTableDTO.setTableName(createTableDTO.getTableName() + "_" + tenantId);	
			Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(createTableDTO); 
			if (apiResponseDTO.getStatusCode() == 200) {
					apiResponseDTO.setMessage(
							"Table-" + createTableDTO.getTableName().split("_")[0] + ", is created successfully");
				return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
			} else {
				log.info(TABLE +"could not be created: {}", apiResponseDTO);
				throw new CustomException(
					HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION,
							String.format(ERROR_MSG+" Creating Table: %s Having TenantID; %d",createTableDTO.getTableName().split("_")[0], tenantId));
			}
		}
		
	@DeleteMapping("/{tableName}")
	@Operation(summary = "DELETE A TABLE (SOFT DELETE).", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isDeletePermissionEnabled()")
	public ResponseEntity<Response> deleteTable(@RequestParam int tenantId, @PathVariable String tableName) {
		tableName = tableName + "_" + tenantId;
		Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(tenantId, tableName);
		if (apiResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		}else {
	     	log.debug("Exception occurred: {}", apiResponseDTO);
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION, BAD_REQUEST_MSG);
		}
	} 
	
	@PutMapping("/restore/{tableName}")
	@Operation(summary = "RESTORE A DELETED TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isEditPermissionEnabled()")
	public ResponseEntity<Response> restoreTable(@RequestParam int tenantId, @PathVariable String tableName) {
		tableName = tableName + "_" + tenantId;
		String tableNameForMessage = tableName.split("_")[0];
		Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName);
		if (apiResponseDTO.getStatusCode() == 200) {
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
			log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
					HttpStatusCode.BAD_REQUEST_EXCEPTION,String.format(ERROR_MSG+ "Restoring for %s %s ", TABLE, tableName.split("_")[1]));
		}
	}

	@PutMapping("/{tableName}")
	@Operation(summary = "REPLACE SCHEMA OF AN EXISTING TABLE.", security = @SecurityRequirement(name = "bearerAuth"))
	@PreAuthorize(value = "@keycloakUserPermission.isEditPermissionEnabled()")
	public ResponseEntity<Response> updateTableSchema(@RequestParam int tenantId, @PathVariable String tableName,
			@RequestBody ManageTable newTableSchemaDTO) {
		tableName = tableName + "_" + tenantId;
		newTableSchemaDTO.setTableName(tableName);
		Response apiResponseDTO = manageTableServicePort.updateTableSchema(tenantId, tableName.split("_")[0],
					newTableSchemaDTO);
		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO.setMessage("Table is updated successfully");
			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {
		   throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),
						HttpStatusCode.BAD_REQUEST_EXCEPTION, BAD_REQUEST_MSG);
	    }
	}
	
}
