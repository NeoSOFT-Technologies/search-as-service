package com.searchservice.app.domain.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.common.SolrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.GetCapacityPlan;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.dto.table.TableSchemav2.TableSchemav2Data;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.BasicUtil;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.TableSchemaParser;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SolrJAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.ContentNotFoundException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;
import com.searchservice.app.rest.errors.InvalidSKUOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.OperationIncompleteException;
import com.searchservice.app.rest.errors.SolrSchemaValidationException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageTableService implements ManageTableServicePort {
	private static final String TABLE_NOT_FOUND_MSG = "Table: %s, does not exist";
	// Schema
	private static final String SEARCH_EXCEPTION_MSG = "The table - {} is Not Found in the Search Cloud!";
	private static final String SEARCH_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private static final String SCHEMA_UPDATE_SUCCESS = "Schema is updated successfully";
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String VALIDATED = "validated";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	private static final String DEFAULT_CONFIGSET = "_default";
	private static final String SIMPLE_DATE_FORMATTER = "dd-M-yyyy hh:mm:ss";
	private static final String FILE_CREATE_ERROR = "Error File Creating File {}";
	private final Logger logger = LoggerFactory.getLogger(ManageTableService.class);

	@Value("${base-search-url}")
	private String searchURL;
	@Value("${basic-auth.username}")
	private String basicAuthUsername;
	@Value("${basic-auth.password}")
	private String basicAuthPassword;
	// ConfigSet
	@Value("${base-configset}")
	private String baseConfigSet;

	// UPDATE Table
	@Value("${table-schema-attributes.delete-file-path}")
	String deleteSchemaAttributesFilePath;

	@Value("${table-schema-attributes.days}")
	long schemaDeleteDuration;

	SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMATTER);

	private String servicename = "Manage_Table_Service";
	private String username = "Username";

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Autowired
	SearchAPIAdapter searchAPIAdapter;
	HttpSolrClient searchClient;

	@Autowired
	SolrJAdapter solrjAdapter;

	public ManageTableService(String searchUrl, SearchAPIAdapter searchAPIAdapter, HttpSolrClient searchClient) {
		this.searchURL = searchUrl;
		this.searchAPIAdapter = searchAPIAdapter;
		this.searchClient = searchClient;
	}

	private void requestMethod(LoggersDTO loggersDTO, String nameofCurrMethod) {

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
	}

	@Override
	public GetCapacityPlan capacityPlans(LoggersDTO loggersDTO) {

		logger.debug("capacity Plans");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		LoggerUtils.printlogger(loggersDTO, false, false);
		return new GetCapacityPlan(capacityPlans);
	}

	@Override
	public Response getTables(int clientId, LoggersDTO loggersDTO) {

		logger.debug("get Tables");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		// CollectionAdminRequest.List

		Response getListItemsResponseDTO = new Response();
		java.util.List<String> data = solrjAdapter.getCollectionAdminRequestList(clientId);

		try {
			data = data.stream().map(datalist -> datalist.split("_" + clientId)[0]).collect(Collectors.toList());

			getListItemsResponseDTO.setData(data);
			getListItemsResponseDTO.setName(nameofCurrMethod);
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all tables");
			String timestamp = LoggerUtils.utcTime().toString();
			loggersDTO.setTimestamp(timestamp);
			LoggerUtils.printlogger(loggersDTO, false, false);
		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setStatusCode(400);
			getListItemsResponseDTO.setMessage("Unable to retrieve tables");
			LoggerUtils.printlogger(loggersDTO, false, true);
		}

		return getListItemsResponseDTO;
	}

	@Override
	public TableSchemav2 getCurrentTableSchema(int clientId, String tableName) {

		if (!isTableExists(tableName + "_" + clientId))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName));

		// GET tableSchema at Search cloud
		TableSchemav2 tableSchema = getTableSchema(tableName + "_" + clientId);

		// Compare tableSchema locally Vs. tableSchema at Search cloud
		TableSchemav2 schemaResponse = compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(tableName, clientId,
				tableSchema);
		schemaResponse.getData().setColumns(schemaResponse.getData().getColumns().stream()
				.filter(s -> !s.getName().startsWith("_")).collect(Collectors.toList()));
		// tes
		logger.info("returning resp from getCurrSchema ######");

		return schemaResponse;
	}

	@Override
	public TableSchemav2 getTableSchemaIfPresent(String tableName, LoggersDTO loggersDTO) {

		logger.debug("get Table Schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		if (!isTableExists(tableName))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName.split("_")[0]));
		TableSchemav2 tableSchema = getTableSchema(tableName);
		tableSchema.getData().setColumns(tableSchema.getData().getColumns().stream()
				.filter(s -> !s.getName().startsWith("_")).collect(Collectors.toList()));
		LoggerUtils.printlogger(loggersDTO, false, false);

		return tableSchema;
	}

	@Override
	public Map<Object, Object> getTableDetails(String tableName, LoggersDTO loggersDTO) {

		logger.debug("get Table Details");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
		Map<Object, Object> finalResponseMap = new HashMap<>();

		try {

			finalResponseMap = solrjAdapter.getTableFetailsFromSolrjCluster(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			finalResponseMap.put("Error", "Error connecting to cluster.");
			return finalResponseMap;
		}
		if (!finalResponseMap.containsKey("tableDetails") || finalResponseMap.get("tableDetails") == null) {
			finalResponseMap = new HashMap<>();
			finalResponseMap.put("Error", "Invalid table name provided.");
			LoggerUtils.printlogger(loggersDTO, false, true);
			return finalResponseMap;
		} else {

			LoggerUtils.printlogger(loggersDTO, false, false);
			return finalResponseMap;
		}
	}

	@Override
	public Response createTableIfNotPresent(ManageTable manageTableDTO, LoggersDTO loggersDTO) {

		logger.debug("create Table If Not Present");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		if (isTableExists(manageTableDTO.getTableName()))
			throw new BadRequestOccurredException(400, manageTableDTO.getTableName() + " table already exists");

		// Configset is present, proceed
		Response apiResponseDTO = createTable(manageTableDTO);

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		if (apiResponseDTO.getStatusCode() == 200) {
			// Check if new table columns are to be added(Non-null list of columns)
			if (manageTableDTO.getColumns() == null) {
				String updatedMsg = String.format("%s. No new columns found", apiResponseDTO.getMessage());
				apiResponseDTO.setMessage(updatedMsg);
				return apiResponseDTO;
			} else if (manageTableDTO.getColumns().isEmpty())
				return apiResponseDTO;

			// Add schemaAttributes
			TableSchema tableSchemaDTO = new TableSchema(manageTableDTO.getTableName(), DEFAULT_CONFIGSET,
					manageTableDTO.getColumns());
			Response tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);
			logger.info("Adding schema attributes response: {}", tableSchemaResponseDTO.getMessage());

			LoggerUtils.printlogger(loggersDTO, false, false);
		} else if (apiResponseDTO.getStatusCode() == 400) {
			LoggerUtils.printlogger(loggersDTO, false, true);
		}
		return apiResponseDTO;
	}

	@Override
	public Response deleteTable(String tableName, LoggersDTO loggersDTO) {

		logger.debug("delete Table");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		if (!isTableExists(tableName))
			throw new ContentNotFoundException(404, String.format(TABLE_NOT_FOUND_MSG, tableName.split("_")[0]));

		// Delete table

		Response apiResponseDTO = new Response();

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		if (solrjAdapter.deleteTableFromSolrj(tableName)) {

			apiResponseDTO.setStatusCode(200);
			LoggerUtils.printlogger(loggersDTO, false, false);
			apiResponseDTO.setMessage("Table: " + tableName + ", is successfully deleted");

		} else {
			apiResponseDTO.setStatusCode(400);
			LoggerUtils.printlogger(loggersDTO, false, true);
			apiResponseDTO.setMessage("Unable to delete table: " + tableName);

		}
		return apiResponseDTO;
	}

	@Override
	public Response updateTableSchema(int clientId, String tableName, TableSchema tableSchemaDTO,
			LoggersDTO loggersDTO) {

		logger.debug("update Table Schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);

		Response apiResponseDTO = new Response();

		// Compare tableSchema locally Vs. tableSchema at solr cloud
		checkForSchemaSoftDeletion(clientId, tableName, tableSchemaDTO.getColumns());

		// ADD new schema fields to the table
		Response tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);

		apiResponseDTO.setStatusCode(tableSchemaResponseDTO.getStatusCode());
		apiResponseDTO.setMessage(tableSchemaResponseDTO.getMessage());
		logger.info("New attributes addition response: {}", apiResponseDTO.getMessage());

		// UPDATE existing schema attributes
		apiResponseDTO = updateSchemaAttributes(tableSchemaDTO);
		logger.info("Existing attributes update response: {}", apiResponseDTO.getMessage());

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);

		LoggerUtils.printlogger(loggersDTO, false, false);
		return apiResponseDTO;
	}

	// AUXILIARY methods implementations >>>>>>>>>>>>>>>>>>
	@Override
	public boolean isConfigSetExists(String configSetName) {
		Response configSets = getConfigSets();
		if (configSetName != null)
			return configSets.getData().contains(configSetName);
		else
			throw new NullPointerOccurredException(404, "Could not fetch any configset, null returned");
	}

	@Override
	public Response getConfigSets() {

		Response getListItemsResponseDTO = new Response();
		try {
			getListItemsResponseDTO.setData(solrjAdapter.getConfigSetFromSolrj());
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all config sets");
		} catch (Exception e) {
			getListItemsResponseDTO.setStatusCode(400);
			getListItemsResponseDTO.setMessage("Configsets could not be retrieved. Error occured");
			logger.error("Error caused while retrieving configsets. Exception: ", e);
		}
		return getListItemsResponseDTO;
	}

	@Override
	public boolean isTableExists(String tableName) {

		try {

			List<String> allTables = solrjAdapter.getAllTablesList();
			return allTables.contains(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			if ((e instanceof SolrServerException)
					&& (HttpHostConnectException) e.getCause() instanceof HttpHostConnectException)
				throw new BadRequestOccurredException(503, "Could not connect to Solr server");
			else
				throw new BadRequestOccurredException(400, "Table Search operation could not be completed");
		}
	}

	@Override
	public TableSchemav2 compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int clientId,
			TableSchemav2 tableSchema) {

		TableSchemav2Data data = new TableSchemav2Data();
		data.setTableName(tableName);

		List<SchemaField> schemaAttributesCloud = tableSchema.getData().getColumns();

		// READ from SchemaDeleteRecord.csv and exclude the deleted attributes
		List<String> deletedSchemaAttributesNames = readSchemaInfoFromSchemaDeleteManager(clientId, tableName);

		// Prepare the final tableSchema to return
		List<SchemaField> schemaAttributesFinal = new ArrayList<>();
		List<String> schemaAttributesToSkipNames = new ArrayList<>();
		// Note down schemaAttributes to skip
		for (SchemaField dto : schemaAttributesCloud) {
			if (!deletedSchemaAttributesNames.contains(dto.getName())) {
				schemaAttributesFinal.add(dto);
			}
			schemaAttributesToSkipNames.add(dto.getName());
		}
		data.setColumns(schemaAttributesFinal);
		tableSchema.setData(data);

		return tableSchema;
	}

	@Override
	public TableSchemav2 getTableSchema(String tableName) {
		logger.info("Getting table schema");

		TableSchemav2 tableSchemaResponseDTO = new TableSchemav2();
		TableSchemav2Data data = new TableSchemav2Data();

		try {

			logger.info("Get request has been processed. Setting status code = 200");

			List<Map<String, Object>> schemaFields = solrjAdapter.getSchemaFields(tableName);
			tableSchemaResponseDTO.setStatusCode(200);
			int numOfFields = schemaFields.size();
			List<SchemaField> solrSchemaFieldDTOs = new ArrayList<>();
			logger.info("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {

				// Prepare the SolrFieldDTO
				SchemaField solrFieldDTO = new SchemaField();
				solrFieldDTO.setName((String) f.get("name"));

				// Parse Field Type Object(String) to Enum
				String solrFieldType = SchemaFieldType.fromSearchFieldTypeToStandardDataType((String) f.get("type"),
						f.get(MULTIVALUED));

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParser.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs.add(solrFieldDTO);
				schemaFieldIdx++;
			}
			logger.info("Total fields stored in attributes array: {}", schemaFieldIdx);

			// prepare response dto
			data.setTableName(tableName.split("_")[0]);
			data.setColumns(solrSchemaFieldDTOs);
			tableSchemaResponseDTO.setData(data);
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SEARCH_EXCEPTION_MSG, tableName);
			logger.info(e.toString());
		}

		return tableSchemaResponseDTO;
	}

	@Override
	public Response createConfigSet(ConfigSet configSetDTO) {

		Response apiResponseDTO = new Response();

		if (solrjAdapter.createConfigSetInSolrj(configSetDTO)) {

			apiResponseDTO = new Response(200, "ConfigSet is created successfully");
		} else {
			apiResponseDTO.setMessage("ConfigSet could not be created");
			apiResponseDTO.setStatusCode(400);
			logger.error("Error caused while creating ConfigSet");
		}
		return apiResponseDTO;
	}

	@Override
	public Response createTable(ManageTable manageTableDTO) {
		logger.info("creating table..");
		Response apiResponseDTO = new Response();

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		CapacityPlanProperties.Plan selectedCapacityPlan = null;

		for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
			if (capacityPlan.getSku().equals(manageTableDTO.getSku())) {
				selectedCapacityPlan = capacityPlan;
			}
		}

		if (selectedCapacityPlan == null) {
			// INVALD SKU
			throw new InvalidSKUOccurredException(HttpStatusCode.INVALID_SKU_NAME.getCode(),
					HttpStatusCode.INVALID_SKU_NAME.getMessage() + " : " + manageTableDTO.getSku());
		}

		if (solrjAdapter.createTableInSolrj(manageTableDTO, selectedCapacityPlan)) {

			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage("Successfully created table: " + manageTableDTO.getTableName());
		} else {
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		}
		return apiResponseDTO;
	}

	@Override
	public Response addSchemaAttributes(TableSchema newTableSchemaDTO) {
		logger.info("Add schema attributes");

		Response tableSchemaResponseDTO = new Response();

		// String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic

			// schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = solrjAdapter.addSchemaAttributesInSolrj(newTableSchemaDTO);

			// Add new fields present in the Target Schema to the given collection/table
			// schema
			List<SchemaField> newAttributes = newTableSchemaDTO.getColumns();
			Map<String, SchemaField> newAttributesHashMap = BasicUtil.convertSchemaFieldListToHashMap(newAttributes);
			logger.info("Target Schema attributes : {}", newAttributes);
			// ####### Add Schema Fields logic #######
//			UpdateResponse addFieldResponse;
//			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;

			List<String> existingAttributesNames = new ArrayList<>();
			for (int i = 0; i < newAttributes.size(); i++) {

				SchemaField fieldDto = newAttributes.get(i);

				boolean isPresent = false;
				for (Map<String, Object> field : schemaFields) {

					if (field.get("name").equals(fieldDto.getName())) {
						isPresent = true;
						existingAttributesNames.add(fieldDto.getName());
						break;
					}
				}
				if (!isPresent)
					newFieldFound = true;
			}
			// If No new schema attribute is found, RETURN
			if (!newFieldFound) {
				tableSchemaResponseDTO.setStatusCode(400);
				tableSchemaResponseDTO.setMessage("No new attributes found");
				return tableSchemaResponseDTO;
			} else {
				if (!existingAttributesNames.isEmpty()) {
					// REMOVE existing attributess from newAttributes list
					for (String attributeName : existingAttributesNames) {
						newAttributesHashMap.remove(attributeName);
					}
				}
			}

			if (newAttributesHashMap.isEmpty()) {
				tableSchemaResponseDTO.setStatusCode(405);
				tableSchemaResponseDTO.setMessage("No new fields found; add attributes operation NOT ALLOWED");
			} else {
				for (Map.Entry<String, SchemaField> fieldDtoEntry : newAttributesHashMap.entrySet()) {
					SchemaField fieldDto = fieldDtoEntry.getValue();
					if (!TableSchemaParser.validateSchemaField(fieldDto)) {
						logger.info("Validation failed for SolrFieldDTO before updating the current schema- {}");
						tableSchemaResponseDTO.setStatusCode(400);
						break;
					}
					if (fieldDto.isSortable()) {
						fieldDto.setMultiValue(false); // For SortOnField UseCase MultiValue must be False
					}
					errorCausingField = fieldDto.getName();
					Map<String, Object> newField = new HashMap<>();

					// if partial search enabled
					if (fieldDto.isPartialSearch()) {
						Map<String, Object> fieldTypeAttributes = new HashMap<>();
						// Add <partial-search> field-type if not present already
						if (!isPartialSearchFieldTypePresent(newTableSchemaDTO.getTableName())) {
							fieldTypeAttributes = getFieldTypeAttributesForPartialSearch();
							solrjAdapter.addFieldTypeRequest(fieldTypeAttributes, newTableSchemaDTO.getTableName());
						} else
							fieldTypeAttributes.put("name", PARTIAL_SEARCH);

						// Add <partial-search> fieldType to the field
						newField.put("type", fieldTypeAttributes.get("name"));
						// Since "partial search" is enabled on this field, docValues has to be disabled
						fieldDto.setSortable(false);
					} else {
						newField.put("type", SchemaFieldType.fromStandardDataTypeToSearchFieldType(fieldDto.getType(),
								fieldDto.isMultiValue()));
						newField.put(DOCVALUES, fieldDto.isSortable());
					}

					newField.put("name", fieldDto.getName());
					newField.put(REQUIRED, fieldDto.isRequired());
					newField.put(STORED, fieldDto.isStorable());
					newField.put(MULTIVALUED, fieldDto.isMultiValue());
					newField.put(INDEXED, fieldDto.isFilterable());

					solrjAdapter.addFieldRequestInSolrj(newField, fieldDto.getName(), newTableSchemaDTO.getTableName());

				}
				tableSchemaResponseDTO.setStatusCode(200);
				tableSchemaResponseDTO.setMessage("New attributes are added successfully");
			}

		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table " + e.getMessage());

			logger.error(SEARCH_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.info(e.toString());
		}
		return tableSchemaResponseDTO;
	}

	@Override
	public Response updateSchemaAttributes(TableSchema newTableSchemaDTO) {
		logger.info("Update Table Schema");

		Response apiResponseDTO = new Response();

		Response schemaResponseDTOBefore = new Response();

		try {

			schemaResponseDTOBefore.setStatusCode(200);

			List<Map<String, Object>> schemaFields = solrjAdapter.updateSchemaAttributesInSolrj(newTableSchemaDTO);
			int numOfFields = schemaFields.size();
			logger.info("Total number of fields: {}", numOfFields);

			// Get all fields from incoming(from req Body) schemaDTO
			List<SchemaField> newSchemaFields = newTableSchemaDTO.getColumns();
			List<Map<String, Object>> targetSchemafields = TableSchemaParser
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);

			// Validate Table Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if (validationEntry.containsKey(VALIDATED)) {
				Object validatedFields = validationEntry.get(VALIDATED);
				if (validatedFields.equals(false))
					throw new SolrSchemaValidationException("Target Schema Fields validation failed!");
			}

			int totalUpdatesRequired = newSchemaFields.size();

			// Update Schema Logic

			Response schemaResponseDTOAfter = solrjAdapter.updateSchemaLogic(newTableSchemaDTO, targetSchemafields);
			if (schemaResponseDTOAfter.getStatusCode() == 200) {
				apiResponseDTO.setStatusCode(200);
				apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			}
			// Compare required Vs Updated Fields
			logger.info("Total field updates required in the current schema: {}", totalUpdatesRequired);
		} catch (NullPointerException e) {

			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error("Null value detected!", e);
			logger.error(e.toString());
		} catch (SolrException e) {
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error(SEARCH_EXCEPTION_MSG + " Existing schema fields couldn't be updated!",
					newTableSchemaDTO.getTableName());
			logger.error(e.toString());
		} catch (SolrSchemaValidationException e) {
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error("Error Message: {}", e.getMessage());
			logger.error(e.toString());
		}
		return apiResponseDTO;
	}

	@Override
	public Response addAliasTable(String tableOriginalName, String tableAlias) {

		Response apiResponseDTO = new Response();

		apiResponseDTO = solrjAdapter.addAliasTableInSolrj(tableOriginalName, tableAlias);
		if (apiResponseDTO.getStatusCode() == 200) {
			apiResponseDTO
					.setMessage("Successfully renamed Solr Collection: " + tableOriginalName + " to " + tableAlias);
		} else {
			apiResponseDTO.setMessage("Unable to rename Solr Collection: " + tableOriginalName + ". Exception.");
		}
		return apiResponseDTO;
	}

	@Override
	public Response deleteConfigSet(String configSetName) {

		Response apiResponseDTO = new Response();

		if (solrjAdapter.deleteConfigSetFromSolrj(configSetName)) {
			apiResponseDTO = new Response(200, "ConfigSet got deleted successfully");
		} else {
			apiResponseDTO.setMessage("ConfigSet could not be deleted");
			apiResponseDTO.setStatusCode(401);

		}
		return apiResponseDTO;
	}

	// Table schema deletion
	public void checkForSchemaSoftDeletion(int clientId, String tableName, List<SchemaField> schemaColumns) {

		List<SchemaField> existingSchemaAttributes = getTableSchema(tableName + "_" + clientId).getData().getColumns();

		for (SchemaField existingSchemaAttribute : existingSchemaAttributes) {

			String exsitingSchemaName = existingSchemaAttribute.getName();
			boolean isContains = ManageTableUtil.checkIfListContainsSchemaColumn(schemaColumns,
					existingSchemaAttribute);

			if (!(exsitingSchemaName.equalsIgnoreCase("_nest_path_") || exsitingSchemaName.equalsIgnoreCase("_root_")
					|| exsitingSchemaName.equalsIgnoreCase("_text_") || exsitingSchemaName.equalsIgnoreCase("_version_")
					|| exsitingSchemaName.equalsIgnoreCase("id")) && !isContains) {
				initializeSchemaDeletion(clientId, tableName, existingSchemaAttribute.getName());
			}
		}

	}

	public void initializeSchemaDeletion(int clientId, String tableName, String columnName) {
		File file = new File(deleteSchemaAttributesFilePath + ".csv");
		checkIfSchemaFileExist(file);
		try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw)) {
			String newRecord = clientId + "," + tableName + "," + formatter.format(Calendar.getInstance().getTime())
					+ "," + columnName;
			bw.write(newRecord);
			bw.newLine();
			logger.debug("Schema {} Succesfully Initialized For Deletion ", columnName);
		} catch (IOException e) {
			logger.error("Error While Intializing Deletion for Schema :{} ", columnName);
		}
	}

	// Soft Delete Table Schema Info Retrieval
	public List<String> readSchemaInfoFromSchemaDeleteManager(int clientId, String tableName) {
		List<String> deletedSchemaAttributes = new ArrayList<>();
		File file = new File(deleteSchemaAttributesFilePath + ".csv");
		checkIfSchemaFileExist(file);
		try (FileReader fr = new FileReader(file)) {
			BufferedReader br = new BufferedReader(fr);
			int lineNumber = 0;
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split(",");
					if (currentRecordData[0].equalsIgnoreCase(String.valueOf(clientId))
							&& currentRecordData[1].equalsIgnoreCase(String.valueOf(tableName))) {
						deletedSchemaAttributes.add(currentRecordData[3]);
						logger.debug("Column {} was requested to be deleted, so skipping it", currentRecordData[3]);
					}
				}
				lineNumber++;
			}

		} catch (Exception e) {
			logger.error("Soft Delete SchemaInfo could not be retrieved");
			throw new OperationIncompleteException(500, "Soft Delete SchemaInfo could not be retrieved");
		}

		return deletedSchemaAttributes;
	}

	public void checkForSchemaDeletion() {
		File existingSchemaFile = new File(deleteSchemaAttributesFilePath + ".csv");
		checkIfSchemaFileExist(existingSchemaFile);
		File newSchemaFile = new File(deleteSchemaAttributesFilePath + "Temp" + ".csv");
		int lineNumber = 0;
		int schemaDeleteRecordCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(existingSchemaFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newSchemaFile))) {
			String currentSchemaDeleteRecord;
			while ((currentSchemaDeleteRecord = br.readLine()) != null) {
				if (lineNumber != 0) {
					long diff = checkDatesDifference(currentSchemaDeleteRecord);
					if (diff < schemaDeleteDuration) {
						pw.println(currentSchemaDeleteRecord);
					} else {
						if (performSchemaDeletion(currentSchemaDeleteRecord)) {
							schemaDeleteRecordCount++;

						} else {
							pw.println(currentSchemaDeleteRecord);
						}
					}
				} else {
					pw.println(currentSchemaDeleteRecord);
				}
				lineNumber++;
			}
			pw.flush();
			makeDeleteTableFileChangesForDelete(newSchemaFile, existingSchemaFile, schemaDeleteRecordCount);
		} catch (IOException exception) {
			logger.error("Error While Performing Schema Deletion ", exception);
		}
	}

	public long checkDatesDifference(String currentSchemaDeleteRecord) {
		try {
			String date = currentSchemaDeleteRecord.split(",")[2];
			Date requestDate = formatter.parse(date);
			Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
			long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
			return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			logger.error("Error!", e);
			return 0;
		}
	}

	public boolean performSchemaDeletion(String schemaDeleteData) {
		String columnName = schemaDeleteData.split(",")[3];
		String tableName = schemaDeleteData.split(",")[1];
		int schemaDeletionStatus = solrjAdapter.performSchemaDeletion(columnName, tableName);
		if (schemaDeletionStatus == 200) {
			logger.debug("Schema {} Succesfully Deleted ", columnName);
			return true;
		} else {
			logger.debug("Schema {} Deletion Failed ", columnName);
			return false;
		}
	}

	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile, int schemaDeleteRecordCount) {
		File schemaDeleteRecordFile = new File(deleteSchemaAttributesFilePath + ".csv");
		checkIfSchemaFileExist(schemaDeleteRecordFile);
		if (existingFile.delete() && newFile.renameTo(schemaDeleteRecordFile)) {
			checkTableDeletionStatus(schemaDeleteRecordCount);
		}
	}

	public boolean checkTableDeletionStatus(int schemaDeleteRecordCount) {
		if (schemaDeleteRecordCount > 0) {
			logger.debug("Total Number of Schema's Found and Deleted: {}", schemaDeleteRecordCount);
			return true;
		} else {
			logger.debug("No Schema Records Were Found and Deleted With Request More Or Equal To 15 days");
			return false;
		}
	}

	@Override
	public boolean checkIfTableNameisValid(String tableName) {
		if (null == tableName || tableName.isBlank() || tableName.isEmpty())
			throw new InvalidInputOccurredException(HttpStatusCode.INVALID_TABLE_NAME.getCode(),
					"Provide valid Table Name");
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
		Matcher matcher = pattern.matcher(tableName);
		return matcher.find();
	}

	// Partial Search Field Type
	public static Map<String, Object> getFieldTypeAttributesForPartialSearch() {
		final String FIELD_TYPE_CLASS = "class";
		final String FIELD_TYPE_NAME = "name";

		Map<String, Object> partialSearchFieldTypeAttrs = new HashMap<>();
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_CLASS, "solr.TextField");
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_NAME, "partial_search");
		partialSearchFieldTypeAttrs.put("positionIncrementGap", "100");

		Map<String, Object> analyzerObject = new HashMap<>();
		// Prepare charFilters
		Map<String, Object> charFilter = new HashMap<>();
		charFilter.put(FIELD_TYPE_CLASS, "solr.PatternReplaceCharFilterFactory");
		charFilter.put("replacement", "$1$1");
		charFilter.put("pattern", "([a-zA-Z])\\\\1+");
		// Prepare tokenizer
		Map<String, Object> tokenizerObject = new HashMap<>();
		tokenizerObject.put(FIELD_TYPE_CLASS, "solr.WhitespaceTokenizerFactory");
		// Prepare filters
		Map<String, Object> filterObject1 = new HashMap<>();
		Map<String, Object> filterObject2 = new HashMap<>();
		filterObject1.put(FIELD_TYPE_CLASS, "solr.WordDelimiterFilterFactory");
		filterObject1.put("preserveOriginal", "0");
		filterObject2.put(FIELD_TYPE_CLASS, "solr.NGramTokenizerFactory");
		filterObject2.put("maxGramSize", "25");
		filterObject2.put("minGramSize", "3");
		Map<String, Object> filtersObject = new HashMap<>();
		filtersObject.put("filters", Arrays.asList(filterObject1, filterObject2));
		// Add charFilters, tokenizer & filters to analyzer
		analyzerObject.put("charFilters", Arrays.asList(charFilter));
		analyzerObject.put("tokenizer", tokenizerObject);
		analyzerObject.put("filters", Arrays.asList(filterObject1, filterObject2));

		Object analyzerFinalObject = analyzerObject;
		partialSearchFieldTypeAttrs.put("analyzer", analyzerFinalObject);

		return partialSearchFieldTypeAttrs;
	}

	public boolean isPartialSearchFieldTypePresent(String tableName) {

		try {

			List<FieldTypeDefinition> fieldTypes = solrjAdapter.isPartialSearchFieldInSolrj(tableName);

			return fieldTypes.stream().anyMatch(ft -> ft.getAttributes().containsValue(PARTIAL_SEARCH));
		} catch (Exception e) {

		}
		return false;
	}

	public boolean checkIfSchemaFileExist(File file) {
		if (!file.exists()) {
			try {
				boolean createFile = file.createNewFile();
				if (createFile) {
					logger.debug("File With Name: {} Created Succesfully", file.getName());
				}
				return true;
			} catch (IOException e) {
				logger.error(FILE_CREATE_ERROR, file.getName(), e);
				return false;
			}
		} else {
			return false;
		}
	}

}
