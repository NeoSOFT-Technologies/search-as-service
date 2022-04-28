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
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.CapacityPlanResponse;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.dto.table.TableSchemav2;
import com.searchservice.app.domain.dto.table.TableSchemav2.TableSchemav2Data;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.BasicUtil;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.domain.utils.TableSchemaParserUtil;
import com.searchservice.app.domain.utils.TypeCastingUtil;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SearchJAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.searchservice.app.rest.errors.InvalidColumnNameException;
import com.searchservice.app.rest.errors.InvalidInputOccurredException;
import com.searchservice.app.rest.errors.InvalidSKUOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
import com.searchservice.app.rest.errors.OperationIncompleteException;
import com.searchservice.app.rest.errors.TableAlreadyExistsException;
import com.searchservice.app.rest.errors.TableNotFoundException;
import com.searchservice.app.rest.errors.WrongMultiValueTypeException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManageTableService implements ManageTableServicePort {
	private static final String NAME = "name";
	private static final String TABLE_NOT_FOUND_MSG = "Table: %s, does not exist";
	// Schema
	private static final String SEARCH_EXCEPTION_MSG = "The table - {} is Not Found in the Search Cloud!";
	private static final String SEARCH_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private static final String SCHEMA_UPDATE_SUCCESS = "Schema is updated successfully";
	private static final String MULTIVALUED = "multiValued";
	private static final String SIMPLE_DATE_FORMATTER = "dd-M-yyyy hh:mm:ss";
	private static final String FILE_CREATE_ERROR = "Error File Creating File {}";
	private static final String TABLE = "Table ";
	private final Logger logger = LoggerFactory.getLogger(ManageTableService.class);

	@Value("${base-search-url}")

	private String searchNonStatic;
	@Value("${basic-auth.username}")

	private String basicAuthUsernameNonStatic;
	@Value("${basic-auth.password}")

	private String basicAuthPasswordNonStatic;

	// ConfigSet
	@Value("${base-configset}")

	private String baseConfigSetNonStatic;

	// UPDATE Table
	@Value("${table-schema-attributes.delete-file-path}")

	private String deleteSchemaAttributesFilePathNonStatic;
	@Value("${table-schema-attributes.days}")

	private long schemaDeleteDurationNonStatic;

	// Init configurations
	private static String searchURL;
	private static String deleteSchemaAttributesFilePath;
	private static long schemaDeleteDuration;

	@Autowired
	public ManageTableService(@Value("${base-search-url}") String solrURLNonStatic,
			@Value("${table-schema-attributes.delete-file-path}") String deleteSchemaAttributesFilePathNonStatic,
			@Value("${table-schema-attributes.days}") long schemaDeleteDurationNonStatic) {

		searchURL = solrURLNonStatic;
		deleteSchemaAttributesFilePath = deleteSchemaAttributesFilePathNonStatic;
		schemaDeleteDuration = schemaDeleteDurationNonStatic;
	}

	SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMATTER);

	private String servicename = "Manage_Table_Service";
	private String username = "Username";

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Autowired
	SearchAPIAdapter searchAPIAdapter;
	HttpSolrClient searchClient;
	
	@Autowired
	SearchJAdapter searchJAdapter;
	

	@Override
	public CapacityPlanResponse capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();

		return new CapacityPlanResponse(200, "Successfully retrieved all Capacity Plans", capacityPlans);
	}

	@Override
	public Response getTables(int tenantId) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		Response getListItemsResponseDTO = new Response();

		CollectionAdminResponse response = searchJAdapter.getCollectionAdminRequestList(searchClientActive);
		java.util.List<String> data = TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"),
				tenantId);

		try {
			data = data.stream().map(datalist -> datalist.split("_" + tenantId)[0]).collect(Collectors.toList());

			getListItemsResponseDTO.setData(data);
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all tables");

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			getListItemsResponseDTO.setMessage("Unable to retrieve tables");

		}

		return getListItemsResponseDTO;
	}

	@Override
	public TableSchemav2 getCurrentTableSchema(int tenantId, String tableName) {

		if (!isTableExists(tableName + "_" + tenantId))
			throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),
					TABLE + tableName + " having TenantID: " + tenantId +" "+HttpStatusCode.TABLE_NOT_FOUND.getMessage());

		// GET tableSchema at Search cloud
		TableSchemav2 tableSchema = getTableSchema(tableName + "_" + tenantId);

		// Compare tableSchema locally Vs. tableSchema at Search cloud
		TableSchemav2 schemaResponse = compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(tableName, tenantId,
				tableSchema);
		schemaResponse.getData().setColumns(schemaResponse.getData().getColumns().stream()
				.filter(s -> !s.getName().startsWith("_")).collect(Collectors.toList()));

		return schemaResponse;
	}

	@Override
	public TableSchemav2 getTableSchemaIfPresent(String tableName) {

		if (!isTableExists(tableName))
			throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(), String.format(TABLE_NOT_FOUND_MSG, tableName.split("_")[0]));
		TableSchemav2 tableSchema = getTableSchema(tableName);

		tableSchema.getData().setColumns(tableSchema.getData().getColumns().stream()
				.filter(s -> !s.getName().startsWith("_")).collect(Collectors.toList()));

		return tableSchema;
	}

	@Override
	public Response createTableIfNotPresent(ManageTable manageTableDTO) {

		if (isTableExists(manageTableDTO.getTableName()))
			throw new TableAlreadyExistsException(HttpStatusCode.TABLE_ALREADY_EXISTS.getCode(), 
					TABLE + manageTableDTO.getTableName().split("_")[0] + " Having TenantID: "+manageTableDTO.getTableName().split("_")[1]
							+" "+HttpStatusCode.TABLE_ALREADY_EXISTS.getMessage());
        
		if(!isColumnNameValid(manageTableDTO.getColumns())) {
			   throw new InvalidColumnNameException(HttpStatusCode.INVALID_COLUMN_NAME.getCode(),
					HttpStatusCode.INVALID_COLUMN_NAME.getMessage());
		}
		
		if (Boolean.FALSE.equals(isValidFormatDataTypeForMultivalued(manageTableDTO.getColumns()))) {

			throw new WrongMultiValueTypeException(HttpStatusCode.WRONG_DATA_TYPE_MULTIVALUED.getCode(),
					HttpStatusCode.WRONG_DATA_TYPE_MULTIVALUED.getMessage());

		}
		// Configset is present, proceed
		Response apiResponseDTO = createTable(manageTableDTO);

		if (apiResponseDTO.getStatusCode() == 200) {
			// Check if new table columns are to be added(Non-null list of columns)
			if (manageTableDTO.getColumns() == null) {
				String updatedMsg = String.format("%s. No new columns found", apiResponseDTO.getMessage());
				apiResponseDTO.setMessage(updatedMsg);
				return apiResponseDTO;
			} else if (manageTableDTO.getColumns().isEmpty())
				return apiResponseDTO;

			// Add schema fields
			TableSchema tableSchemaDTO = new TableSchema(manageTableDTO.getTableName(), manageTableDTO.getColumns());
			Response tableSchemaResponseDTO = addSchemaFields(tableSchemaDTO);
			logger.info("Adding schema attributes response: {}", tableSchemaResponseDTO.getMessage());
		}
		return apiResponseDTO;
	}

	@Override
	public Response deleteTable(String tableName) {

		if (!isTableExists(tableName))
			throw new TableNotFoundException(HttpStatusCode.TABLE_NOT_FOUND.getCode(),
					TABLE + tableName.split("_")[0] + " having TenantID: " + tableName.split("_")[1] + " "+HttpStatusCode.TABLE_NOT_FOUND.getMessage());

		// Delete table
		Response apiResponseDTO = new Response();

		boolean response = searchJAdapter.deleteTableFromSolrj(tableName);
		if (response) {

			apiResponseDTO.setStatusCode(200);

			apiResponseDTO.setMessage("Table: " + tableName + ", is successfully deleted");

		} else {

			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());

			apiResponseDTO.setMessage("Unable to delete table: " + tableName);
		}
		return apiResponseDTO;
	}

	@Override
	public Response updateTableSchema(int tenantId, String tableName, TableSchema tableSchemaDTO) {
		Response apiResponseDTO = new Response();

		// Compare tableSchema locally Vs. tableSchema at solr cloud
		checkForSchemaSoftDeletion(tenantId, tableName, tableSchemaDTO.getColumns());


		if (Boolean.FALSE.equals(isValidFormatDataTypeForMultivalued(tableSchemaDTO.getColumns()))) {

			throw new WrongMultiValueTypeException(HttpStatusCode.WRONG_DATA_TYPE_MULTIVALUED.getCode(),
					HttpStatusCode.WRONG_DATA_TYPE_MULTIVALUED.getMessage());

		}
		
		// ADD new schema fields to the table
		Response tableSchemaResponseDTO = addSchemaFields(tableSchemaDTO);

		apiResponseDTO.setStatusCode(tableSchemaResponseDTO.getStatusCode());
		apiResponseDTO.setMessage(tableSchemaResponseDTO.getMessage());

		// UPDATE existing schema attributes
		apiResponseDTO = updateSchemaFields(tableSchemaDTO);
		
		return apiResponseDTO;
	}

	// AUXILIARY methods implementations >>>>>>>>>>>>>>>>>>
	@Override
	public boolean isConfigSetExists(String configSetName) {
		Response configSets = getConfigSets();
		if (configSetName != null)
			return configSets.getData().contains(configSetName);
		else
			throw new NullPointerOccurredException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), "Could not fetch any configset, null returned");
	}

	@Override
	public Response getConfigSets() {

		Response getListItemsResponseDTO = new Response();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		try {
			ConfigSetAdminResponse configSetResponse = searchJAdapter.getConfigSetFromSolrj(searchClientActive);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			List<String> data = TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets"));
			getListItemsResponseDTO.setData(data);
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all config sets");
		} catch (Exception e) {
			getListItemsResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			getListItemsResponseDTO.setMessage("Configsets could not be retrieved. Error occured");

		}
		return getListItemsResponseDTO;
	}

	@Override
	public boolean isTableExists(String tableName) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		try {
			CollectionAdminResponse response = searchJAdapter.getCollectionAdminRequestList(searchClientActive);
			List<String> allTables = TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
			return allTables.contains(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			if ((e instanceof SolrServerException)
					&& (HttpHostConnectException) e.getCause() instanceof HttpHostConnectException)
				throw new BadRequestOccurredException(HttpStatusCode.SERVER_UNAVAILABLE.getCode(),
						"Could not connect to Solr server");
			else
				throw new BadRequestOccurredException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), "Table Search operation could not be completed");
		}
	}

	@Override
	public TableSchemav2 compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(String tableName, int tenantId,
			TableSchemav2 tableSchema) {

		TableSchemav2Data data = new TableSchemav2Data();
		data.setTableName(tableName);

		List<SchemaField> schemaAttributesCloud = tableSchema.getData().getColumns();

		// READ from SchemaDeleteRecord.csv and exclude the deleted attributes
		List<String> deletedSchemaAttributesNames = readSchemaInfoFromSchemaDeleteManager(tenantId, tableName);

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

		TableSchemav2 tableSchemaResponseDTO = new TableSchemav2();
		TableSchemav2Data data = new TableSchemav2Data();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
		try {
			SchemaResponse schemaResponse = searchJAdapter.getSchemaFields(searchClientActive);

			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			tableSchemaResponseDTO.setStatusCode(200);

			List<SchemaField> solrSchemaFieldDTOs = new ArrayList<>();

			for (Map<String, Object> f : schemaFields) {

				// Prepare the SolrFieldDTO
				SchemaField solrFieldDTO = new SchemaField();
				solrFieldDTO.setName((String) f.get(NAME));

				// Parse Field Type Object(String) to Enum

				String solrFieldType = SchemaFieldType.fromSearchFieldTypeToStandardDataType((String) f.get("type"),
						f.get(MULTIVALUED));

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParserUtil.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs.add(solrFieldDTO);

			}

			// prepare response dto
			data.setTableName(tableName.split("_")[0]);
			data.setColumns(solrSchemaFieldDTOs);
			tableSchemaResponseDTO.setData(data);
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			logger.error(SEARCH_EXCEPTION_MSG, tableName);
			logger.info(e.toString());
		}

		return tableSchemaResponseDTO;
	}

	@Override
	public Response createConfigSet(ConfigSet configSetDTO) {

		Response apiResponseDTO = new Response();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		ConfigSetAdminRequest.Create configSetRequest = new ConfigSetAdminRequest.Create();
		try {
			configSetRequest.setBaseConfigSetName(configSetDTO.getBaseConfigSetName());
			configSetRequest.setConfigSetName(configSetDTO.getConfigSetName());
			configSetRequest.setMethod(METHOD.POST);

			searchJAdapter.createConfigSetInSolrj(configSetRequest, searchClientActive);

			apiResponseDTO = new Response(200, "ConfigSet is created successfully");
		} catch (Exception e) {
			apiResponseDTO.setMessage("ConfigSet could not be created");
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			logger.error("Error caused while creating ConfigSet");
		}
		return apiResponseDTO;
	}

	@Override
	public Response createTable(ManageTable manageTableDTO) {
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

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(manageTableDTO.getTableName(),
				selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());
		HttpSolrClient searchClientActive = new HttpSolrClient.Builder(searchURL).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		try {
			searchJAdapter.createTableInSolrj(request, searchClientActive);
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage("Successfully created table: " + manageTableDTO.getTableName());
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());

			apiResponseDTO.setMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);

		}
		return apiResponseDTO;
	}

	@Override
	public Response addSchemaFields(TableSchema newTableSchemaDTO) {
		// Prepare SearchClient instance
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		Response tableSchemaResponseDTO = new Response();

		String errorCausingField = null;
		String payloadOperation = "";
		try {
			SchemaResponse schemaResponse = searchJAdapter.processSchemaRequest(searchClientActive, schemaRequest);
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			List<Map<String, Object>> schemaFields = retrievedSchema.getFields();
			
			// Prepare final HashMap, and remove already existing schema fields
			List<SchemaField> newFields = newTableSchemaDTO.getColumns();
			Map<String, SchemaField> newFieldsHashMap = BasicUtil.convertSchemaFieldListToHashMap(newFields);
			newFieldsHashMap = ManageTableUtil.removeExistingFields(newFieldsHashMap, newFields, schemaFields);

			if (newFieldsHashMap.isEmpty()) {
				tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
				tableSchemaResponseDTO.setMessage("No new fields found; add attributes operation NOT ALLOWED");
				return tableSchemaResponseDTO;
			} else {
				for (Map.Entry<String, SchemaField> fieldDtoEntry : newFieldsHashMap.entrySet()) {
					SchemaField fieldDto = fieldDtoEntry.getValue();
					payloadOperation = "SchemaRequest.AddField";
					errorCausingField = fieldDto.getName();
					
					if (!TableSchemaParserUtil.validateSchemaField(fieldDto)) {
						logger.info("Validation failed for SolrFieldDTO before updating the current schema");
						tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
						break;
					}
					
					Map<String, Object> newField = new HashMap<>();
					// PARTIAL_SEARCH UPDATE
					searchJAdapter.partialSearchUpdate(newTableSchemaDTO, fieldDto, newField);
					newField = TableSchemaParserUtil.prepareNewField(newField, fieldDto);

					// Add new fields present in the Target Schema to the given collection/table
					SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
					searchJAdapter.addFieldRequestInSolrj(addFieldRequest, searchClientActive);
				}
				tableSchemaResponseDTO.setStatusCode(200);
				tableSchemaResponseDTO.setMessage("New attributes are added successfully");
			}

		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table " + e.getMessage());
			logger.error(SEARCH_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return tableSchemaResponseDTO;
	}

	@Override
	public Response updateSchemaFields(TableSchema newTableSchemaDTO) {
		// Prepare SearchClient instance
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		Response apiResponseDTO = new Response();

		try {
			// Get all fields from incoming(from req Body) schemaDTO and validate
			List<Map<String, Object>> targetSchemafields = searchJAdapter
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);

			int updatedFields = 0;
			for (Map<String, Object> currField : targetSchemafields) {
				// Pass the field to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				searchJAdapter.updateSchemaLogic(searchClientActive, updateFieldsRequest);
				updatedFields++;
				logger.info("Field- {} is successfully updated", currField.get(NAME));
			}
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.debug("Total field updates required in the current schema: {}", newTableSchemaDTO.getColumns().size());
			logger.debug("Total fields updated in the current schema: {}", updatedFields);

		} catch (NullPointerException e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode());
			apiResponseDTO.setMessage("Schema could not be updated successfully");
			logger.error("Null value detected!", e);
		} catch (SolrException e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error(
					SEARCH_EXCEPTION_MSG + " Existing schema fields couldn't be updated!",
					newTableSchemaDTO.getTableName(), 
					e.getMessage());
		}
		return apiResponseDTO;
	}

	@Override
	public Response addAliasTable(String tableOriginalName, String tableAlias) {

		Response apiResponseDTO = new Response();
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		try {
			searchJAdapter.addAliasTableInSolrj(searchClientActive, request);
			apiResponseDTO.setStatusCode(200);
		} catch (Exception e) {
			apiResponseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
			logger.error(e.toString());
		}
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
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();
		try {
			configSetRequest.setMethod(METHOD.DELETE);
			configSetRequest.setConfigSetName(configSetName);
			searchJAdapter.deleteConfigSetFromSolrj(searchClientActive, configSetRequest);
			apiResponseDTO = new Response(200, "ConfigSet got deleted successfully");
		} catch (Exception e) {
			apiResponseDTO.setMessage("ConfigSet could not be deleted");
			apiResponseDTO.setStatusCode(HttpStatusCode.UNAUTHORIZED_EXCEPTION.getCode());
		}
		return apiResponseDTO;
	}

	// Table schema deletion
	public void checkForSchemaSoftDeletion(int tenantId, String tableName, List<SchemaField> schemaColumns) {

		List<SchemaField> existingSchemaAttributes = getTableSchema(tableName + "_" + tenantId).getData().getColumns();

		for (SchemaField existingSchemaAttribute : existingSchemaAttributes) {
			String exsitingSchemaName = existingSchemaAttribute.getName();
			boolean isContains = ManageTableUtil.checkIfListContainsSchemaColumn(schemaColumns,
					existingSchemaAttribute);

			if (!(exsitingSchemaName.equalsIgnoreCase("_nest_path_") || exsitingSchemaName.equalsIgnoreCase("_root_")
					|| exsitingSchemaName.equalsIgnoreCase("_text_") || exsitingSchemaName.equalsIgnoreCase("_version_")
					|| exsitingSchemaName.equalsIgnoreCase("id")) && !isContains) {
				initializeSchemaDeletion(tenantId, tableName, existingSchemaAttribute.getName());
			}
		}

	}

	public void initializeSchemaDeletion(int tenantId, String tableName, String columnName) {
		File file = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(file);
		try (FileWriter fw = new FileWriter(file, true); BufferedWriter bw = new BufferedWriter(fw)) {
			String newRecord = tenantId + "," + tableName + "," + formatter.format(Calendar.getInstance().getTime())
					+ "," + columnName;
			bw.write(newRecord);
			bw.newLine();

			logger.debug("Schema {} Succesfully Initialized For Deletion ", columnName);
		} catch (IOException e) {
			logger.error("Error While Intializing Deletion for Schema :{} ", columnName);
		}
	}

	// Soft Delete Table Schema Info Retrieval
	public List<String> readSchemaInfoFromSchemaDeleteManager(int tenantId, String tableName) {
		List<String> deletedSchemaAttributes = new ArrayList<>();
		File file = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(file);
		try (FileReader fr = new FileReader(file)) {
			BufferedReader br = new BufferedReader(fr);
			int lineNumber = 0;
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split(",");
					if (currentRecordData[0].equalsIgnoreCase(String.valueOf(tenantId))
							&& currentRecordData[1].equalsIgnoreCase(String.valueOf(tableName))) {
						deletedSchemaAttributes.add(currentRecordData[3]);
					}
				}
				lineNumber++;
			}
		} catch (Exception e) {
			logger.error("Soft Delete SchemaInfo could not be retrieved");
			throw new OperationIncompleteException(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), 
					"Soft Delete SchemaInfo could not be retrieved");
		}

		return deletedSchemaAttributes;
	}

	public void checkForSchemaDeletion() {
		File existingSchemaFile = new File(deleteSchemaAttributesFilePath);
		checkIfSchemaFileExist(existingSchemaFile);
		File newSchemaFile = new File(deleteSchemaAttributesFilePath.substring(0, deleteSchemaAttributesFilePath.length()-4)+"Temp.csv");
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
			logger.error("Error! {}", e.getMessage());
			return 0;
		}
	}

	public boolean performSchemaDeletion(String schemaDeleteData) {
		String columnName = schemaDeleteData.split(",")[3];
		String tableName = schemaDeleteData.split(",")[1];

		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
		SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(columnName);
		try {
			UpdateResponse response = searchJAdapter.performSchemaDeletion(searchClientActive, deleteFieldRequest);
			int schemaDeletionStatus = response.getStatus();

			if (schemaDeletionStatus == 200) {
				logger.debug("Schema {} Succesfully Deleted ", columnName);
				return true;
			} else {
				logger.debug("Schema {} Deletion Failed ", columnName);
				return false;
			}
		} catch (Exception e) {
			logger.error("Exception Occured While Performing Deletion for Schema {} " + columnName, e);

		}
		return false;
	}

	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile, int schemaDeleteRecordCount) {
		File schemaDeleteRecordFile = new File(deleteSchemaAttributesFilePath);
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

	@Override
	public boolean isColumnNameValid(List<SchemaField> columns) {
		if(columns == null) {
			return true;
		}else {
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
		boolean columnNameIsValid = true;
		for(SchemaField column: columns) {	
			Matcher matcher = pattern.matcher(column.getName());
			if(matcher.find()) {
				columnNameIsValid = false;
				break;
			}
			
		}
		return columnNameIsValid;
	}
	}
	@Override
	public Boolean isValidFormatDataTypeForMultivalued(List<SchemaField> columns) {
		if(columns == null) {
			return true;
		}else {
		boolean multiValueCheck = true;
		for (SchemaField column : columns) {
			if (Boolean.FALSE.equals(TableSchemaParserUtil.isMultivaluedDataTypePlural(column))) {

				multiValueCheck = false;
				break;
			}
		}
		return multiValueCheck;
		}
	}

}
