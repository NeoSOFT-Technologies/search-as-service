package com.searchservice.app.rest;

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
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.port.api.TableDeleteServicePort;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.DeletionOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

//@RestController
//@RequestMapping("${base-url.api-endpoint.versioned-home}" + "/manage/table")
public class VersionedManageTableResource {

	private final Logger log = LoggerFactory.getLogger(VersionedManageTableResource.class);

	private static final String BAD_REQUEST_MSG = ResponseMessages.BAD_REQUEST_MSG;
	@Autowired
	ManageTableServicePort manageTableServicePort;

	@Autowired
	TableDeleteServicePort tableDeleteServicePort;

	public VersionedManageTableResource(ManageTableServicePort manageTableServicePort,
			TableDeleteServicePort tableDeleteServicePort) {
		this.manageTableServicePort = manageTableServicePort;
		this.tableDeleteServicePort = tableDeleteServicePort;

	}

	@GetMapping("/capacity-plans")
	@Operation(summary = "/get-capacity-plans")
	public GetCapacityPlan capacityPlans() {

		GetCapacityPlan getCapacityPlanDTO = manageTableServicePort.capacityPlans();

		return getCapacityPlanDTO;
	}

	@GetMapping("/{tenantId}")
	@Operation(summary = "/all-tables", security = @SecurityRequirement(name = "bearerAuth"))
	public Response getTables(@PathVariable int tenantId) {
		log.debug("Get all tables");

		Response getListItemsResponseDTO = manageTableServicePort.getTables(tenantId);

		if (getListItemsResponseDTO == null)
			throw new NullPointerOccurredException(404, "Received Null response from 'GET tables' service");
		if (getListItemsResponseDTO.getStatusCode() == 200) {

			return getListItemsResponseDTO;
		} else {

			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}

	@GetMapping("/{tenantId}/{tableName}")
	@Operation(summary = "/get-table-info", security = @SecurityRequirement(name = "bearerAuth"))
	public TableSchemav2 getTable(@PathVariable int tenantId, @PathVariable String tableName) {
		log.debug("Get table info");

		tableName = tableName + "_" + tenantId;
		if (tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			throw new BadRequestOccurredException(400, "Table " + tableName + " is Under Deletion Process");
		} else {
			// GET tableSchema
			TableSchemav2 tableInfoResponseDTO = manageTableServicePort.getTableSchemaIfPresent(tableName);

			if (tableInfoResponseDTO == null)
				throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);

			if (tableInfoResponseDTO.getStatusCode() == 200) {
				tableInfoResponseDTO.setMessage("Table Information retrieved successfully");
				return tableInfoResponseDTO;
			} else {

				throw new BadRequestOccurredException(400, "REST operation couldn't be performed");
			}
		}
	}

	@PostMapping
	@Operation(summary = "/create-table", security = @SecurityRequirement(name = "bearerAuth"))
	public Response createTable(@RequestBody ManageTable manageTableDTO) {

		Response apiResponseDTO = manageTableServicePort.createTableIfNotPresent(manageTableDTO);

		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO.setMessage("Table: " + manageTableDTO.getTableName() + ", is created successfully");

			return apiResponseDTO;
		} else {
			log.debug("Table could not be created: {}", apiResponseDTO);

			throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
		}
	}

	@DeleteMapping("/{tenantId}/{tableName}")
	@Operation(summary = "/delete-table", security = @SecurityRequirement(name = "bearerAuth"))
	public Response deleteTable(@PathVariable String tableName, @PathVariable int tenantId) {

		String tableNameForMessage = tableName;

		tableName = tableName + "_" + tenantId;
		if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {

			if (tableDeleteServicePort.checkTableExistensce(tableName)) {
				Response apiResponseDTO = tableDeleteServicePort.initializeTableDelete(tenantId, tableName);
				if (apiResponseDTO.getStatusCode() == 200) {

					return apiResponseDTO;
				} else {
					log.debug("Exception occurred: {}", apiResponseDTO);

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

		String tableNameForMessage = tableName;

		Response apiResponseDTO = tableDeleteServicePort.undoTableDeleteRecord(tableName);

		if (apiResponseDTO.getStatusCode() == 200) {

			return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
		} else {

			log.debug("Exception Occured While Performing Restore Delete For Table: {} ", tableNameForMessage);
			throw new BadRequestOccurredException(400, tableNameForMessage + " is not available for restoring");
		}
	}

	@PutMapping("/{tableName}")
	@Operation(summary = "/update-table-schema", security = @SecurityRequirement(name = "bearerAuth"))
	public Response updateTableSchema(@PathVariable String tableName, @RequestBody TableSchema newTableSchemaDTO) {

		if (!tableDeleteServicePort.isTableUnderDeletion(tableName)) {
			newTableSchemaDTO.setTableName(tableName);
			Response apiResponseDTO = manageTableServicePort.updateTableSchema(101, tableName, newTableSchemaDTO);

			if (apiResponseDTO.getStatusCode() == 200) {

				return apiResponseDTO;
			} else {

				throw new BadRequestOccurredException(400, BAD_REQUEST_MSG);
			}
		} else {
			throw new DeletionOccurredException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					"Table " + tableName + " is Under Deletion Process");
		}
	}
}
