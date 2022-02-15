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
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTOv2;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.BasicUtil;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SolrUtil;
import com.searchservice.app.domain.utils.TableSchemaParser;
import com.searchservice.app.domain.utils.TypeCastingUtil;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.ContentNotFoundException;
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
	private static final String SOLR_EXCEPTION_MSG = "The table - {} is Not Found in the Solr Cloud!";
	private static final String SOLR_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private static final String SOLR_ADD_ATTRIBUTES_EXCEPTION_MSG = "Schema attributes could not be added to the table!";
	private static final String SCHEMA_UPDATE_SUCCESS = "Schema is updated successfully";
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String VALIDATED = "validated";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private final Logger logger = LoggerFactory.getLogger(ManageTableService.class);

	@Value("${base-solr-url}")
	private String solrURL;
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
	@Value("${schema-delete-record.formatter.table-name}")
	String tableNameFormatter;
	@Value("${schema-delete-record.formatter.request-time}")
	String requestTimeFormatter;
	@Value("${schema-delete-record.formatter.column-name}")
	String columnNameFormatter;
	//SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Autowired
	SolrAPIAdapter solrAPIAdapter;
	HttpSolrClient solrClient;

	SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
	public ManageTableService(String solrUrl, SolrAPIAdapter solrAPIAdapter, HttpSolrClient solrClient) {
		this.solrURL = solrUrl;
		this.solrAPIAdapter = solrAPIAdapter;
		this.solrClient = solrClient;
	}

	
	@Override
	public GetCapacityPlanDTO capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		return new GetCapacityPlanDTO(capacityPlans);
	}

	
	@Override
	public ResponseDTO getTables() {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);

		ResponseDTO getListItemsResponseDTO = new ResponseDTO();
		try {
			CollectionAdminResponse response = request.process(solrClientActive);

			getListItemsResponseDTO
					.setItems(TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections")));
			getListItemsResponseDTO.setResponseStatusCode(200);
			getListItemsResponseDTO.setResponseMessage("Successfully retrieved all tables");

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setResponseStatusCode(400);
			getListItemsResponseDTO.setResponseMessage("Unable to retrieve tables");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return getListItemsResponseDTO;
	}
	

	@Override
	public TableSchemaDTOv2 getCurrentTableSchema(int clientId, String tableName) {
		
		if (!isTableExists(tableName + "_" + clientId))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName));

		// GET tableSchema at solr cloud
		TableSchemaDTO tableSchema = getTableSchema(tableName + "_" + clientId);
		
		// Compare tableSchema locally Vs. tableSchema at solr cloud
		TableSchemaDTO currentTableSchema = compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(
				tableName, clientId, tableSchema);

		return new TableSchemaDTOv2(
				currentTableSchema);
	}
	
	
	@Override
	public TableSchemaDTOv2 getTableSchemaIfPresent(int clientId, String tableName) {
		
		if (!isTableExists(tableName + "_" + clientId))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName));
		
		TableSchemaDTO tableSchema = getTableSchema(tableName + "_" + clientId); 
		return new TableSchemaDTOv2(
				tableSchema);
	}
	
	
	@Override
	public Map<Object, Object> getTableDetails(String tableName, int clientId) {		
		tableName = tableName + "_" + clientId;
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);

		Map<Object, Object> finalResponseMap = new HashMap<>();

		CollectionAdminRequest.ClusterStatus clusterStatus = new CollectionAdminRequest.ClusterStatus();
		CollectionAdminResponse response = null;
		try {
			response = clusterStatus.process(solrClientActive);
		} catch (Exception e) {
			logger.error(e.toString());
			finalResponseMap.put("Error", "Error connecting to cluster.");
			return finalResponseMap;
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}

		finalResponseMap = ManageTableUtil.getTableInfoFromClusterStatusResponseObject(
				response.getResponse().asMap(20), 
				tableName);
	
		if(!finalResponseMap.containsKey("tableDetails")
				|| finalResponseMap.get("tableDetails") == null) {
			finalResponseMap = new HashMap<>();
			finalResponseMap.put("Error", "Invalid table name provided.");
		}
		
		return finalResponseMap;
	}


	@Override
	public ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO) {
		if (isTableExists(manageTableDTO.getTableName()))
			throw new BadRequestOccurredException(400, manageTableDTO.getTableName() + " table already exists");

		if (!isConfigSetExists(manageTableDTO.getSchemaName())) {
			// Create Configset if not present
			logger.info("{} configset is not present, creating..", manageTableDTO.getSchemaName());
			ConfigSetDTO configSetDTO = new ConfigSetDTO(baseConfigSet, manageTableDTO.getSchemaName());
			createConfigSet(configSetDTO);
		}
		// Configset is present, proceed
		ResponseDTO apiResponseDTO = createTable(manageTableDTO);
		if (apiResponseDTO.getResponseStatusCode() == 200) {
			// Add schemaAttributes
			TableSchemaDTO tableSchemaDTO = new TableSchemaDTO(manageTableDTO.getTableName(),
					manageTableDTO.getSchemaName(), manageTableDTO.getAttributes());
			TableSchemaDTO tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);
			logger.info("Adding schema attributes response: {}", tableSchemaResponseDTO.getMessage());
		}
		return apiResponseDTO;
	}


	@Override
	public ResponseDTO deleteTable(String tableName) {
		if (!isTableExists(tableName))
			throw new ContentNotFoundException(404, String.format(TABLE_NOT_FOUND_MSG, tableName));

		// Delete table
		CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(tableName);
		CollectionAdminRequest.DeleteAlias deleteAliasRequest = CollectionAdminRequest.deleteAlias(tableName);
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			deleteAliasRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			deleteAliasRequest.process(solrClientActive);

			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage("Table: " + tableName + ", is successfully deleted");
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Unable to delete table: " + tableName);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}

		// Delete configSet attached to the table
		/*
		 * String configSetName = ""; if(apiResponseDTO.getResponseStatusCode()==200)
		 * apiResponseDTO = deleteConfigSet(configSetName);
		 */
		return apiResponseDTO;
	}

	
	@Override
	public ResponseDTO updateTableSchema(int clientId, String tableName, TableSchemaDTO tableSchemaDTO) {
		tableSchemaDTO.setTableName(tableName+ "_" +clientId);
		ResponseDTO apiResponseDTO = new ResponseDTO();
		
		// Compare tableSchema locally Vs. tableSchema at solr cloud
		checkForSchemaSoftDeletion(clientId, tableName, tableSchemaDTO.getAttributes());
		
		// ADD new schema fields to the table
		TableSchemaDTO tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);
		apiResponseDTO.setResponseStatusCode(tableSchemaResponseDTO.getStatusCode());
		apiResponseDTO.setResponseMessage(tableSchemaResponseDTO.getMessage());
		logger.info("New attributes addition response: {}", apiResponseDTO.getResponseMessage());
		
		// UPDATE existing schema attributes
		apiResponseDTO = updateSchemaAttributes(tableSchemaDTO);
		logger.info("Existing attributes update response: {}", apiResponseDTO.getResponseMessage());
		
		return apiResponseDTO;
	}


	// AUXILIARY methods implementations >>>>>>>>>>>>>>>>>>
	@Override
	public boolean isConfigSetExists(String configSetName) {
		ResponseDTO configSets = getConfigSets();
		if (configSetName != null)
			return configSets.getItems().contains(configSetName);
		else
			throw new NullPointerOccurredException(404, "Could not fetch any configset, null returned");
	}
	
	
	@Override
	public ResponseDTO getConfigSets() {
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();

		ResponseDTO getListItemsResponseDTO = new ResponseDTO();
		try {
			ConfigSetAdminResponse configSetResponse = configSetRequest.process(solrClientActive);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			getListItemsResponseDTO
					.setItems(TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets")));
			getListItemsResponseDTO.setResponseStatusCode(200);
			getListItemsResponseDTO.setResponseMessage("Successfully retrieved all config sets");
		} catch (Exception e) {
			getListItemsResponseDTO.setResponseStatusCode(400);
			getListItemsResponseDTO.setResponseMessage("Configsets could not be retrieved. Error occured");
			logger.error("Error caused while retrieving configsets. Exception: ", e);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return getListItemsResponseDTO;
	}
	

	@Override
	public boolean isTableExists(String tableName) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		try {
			CollectionAdminResponse response = request.process(solrClientActive);
			List<String> allTables = TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
			return allTables.contains(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			throw new BadRequestOccurredException(400, "Table Search operation could not be completed");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
	}
	
	
	@Override
	public TableSchemaDTO compareCloudSchemaWithSoftDeleteSchemaReturnCurrentSchema(
			String tableName, int clientId, TableSchemaDTO tableSchema) {
	
		List<SchemaFieldDTO> schemaAttributesCloud = tableSchema.getAttributes();
		// READ from SchemaDeleteRecord.txt and exclude the deleted attributes
		List<String> deletedSchemaAttributesNames = readSchemaInfoFromSchemaDeleteManager(
				clientId, tableName);

		// Prepare the final tableSchema to return
		List<SchemaFieldDTO> schemaAttributesFinal = new ArrayList<>();
		List<String> schemaAttributesToSkipNames = new ArrayList<>();
		// Note down schemaAttributes to skip
		for(SchemaFieldDTO dto: schemaAttributesCloud) {
			if(!deletedSchemaAttributesNames.contains(dto.getName())) {
				schemaAttributesFinal.add(dto);	
			}
			schemaAttributesToSkipNames.add(dto.getName());
		}
		tableSchema.setAttributes(schemaAttributesFinal);
		
		return tableSchema;
	}
	
	
	@Override
	public TableSchemaDTO getTableSchema(String tableName) {
		logger.info("Getting table schema");

		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO();

		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClientActive);
			logger.info("Get request has been processed. Setting status code = 200");
			tableSchemaResponseDTO.setStatusCode(200);

			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			List<SchemaFieldDTO> solrSchemaFieldDTOs = new ArrayList<>();
			logger.info("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {
				
				// Prepare the SolrFieldDTO
				SchemaFieldDTO solrFieldDTO = new SchemaFieldDTO();
				solrFieldDTO.setName((String) f.get("name"));

				// Parse Field Type Object(String) to Enum
				String solrFieldType = SchemaFieldType.fromSolrFieldTypeToStandardDataType(
						(String) f.get("type"));

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParser.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs.add(solrFieldDTO);
				schemaFieldIdx++;
			}
			logger.info("Total fields stored in attributes array: {}", schemaFieldIdx);

			// prepare response dto
			tableSchemaResponseDTO.setSchemaName(schemaName);
			tableSchemaResponseDTO.setTableName(tableName);
			tableSchemaResponseDTO.setAttributes(solrSchemaFieldDTOs);
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (SolrServerException | IOException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.info(e.toString());
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_EXCEPTION_MSG, tableName);
			logger.info(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return tableSchemaResponseDTO;
	}
	
	
	@Override
	public ResponseDTO createConfigSet(ConfigSetDTO configSetDTO) {
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Create configSetRequest = new ConfigSetAdminRequest.Create();
		ResponseDTO apiResponseDTO = new ResponseDTO();

		configSetRequest.setBaseConfigSetName(configSetDTO.getBaseConfigSetName());
		configSetRequest.setConfigSetName(configSetDTO.getConfigSetName());
		/** configSetRequest.setNewConfigSetProperties(new Properties(969)); */
		configSetRequest.setMethod(METHOD.POST);

		try {
			/**
			 * Authenticate in order to access @schema_designer API
			 */
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClientActive);
			apiResponseDTO = new ResponseDTO(200, "ConfigSet is created successfully");
		} catch (Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be created");
			apiResponseDTO.setResponseStatusCode(400);
			logger.error("Error caused while creating ConfigSet. Exception: ", e);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	

	@Override
	public ResponseDTO createTable(ManageTableDTO manageTableDTO) {
		logger.info("creating table..");
		ResponseDTO apiResponseDTO = new ResponseDTO();

		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		CapacityPlanProperties.Plan selectedCapacityPlan = null;

		for (CapacityPlanProperties.Plan capacityPlan : capacityPlans) {
			if (capacityPlan.getSku().equals(manageTableDTO.getSku())) {
				selectedCapacityPlan = capacityPlan;
			}
		}

		if (selectedCapacityPlan == null) {
			// INVALD SKU
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Invalid SKU: " + manageTableDTO.getSku());
			return apiResponseDTO;
		}

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(manageTableDTO.getTableName(),
				manageTableDTO.getSchemaName(), selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage("Successfully created table: " + manageTableDTO.getTableName());
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO
					.setResponseMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}

	
	@Override
	public TableSchemaDTO addSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.info("Add schema attributes");

		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL, newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO();

		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClientActive);

			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			
			// Add new fields present in the Target Schema to the given collection/table schema
			List<SchemaFieldDTO> newAttributes = newTableSchemaDTO.getAttributes();
			HashMap<String, SchemaFieldDTO> newAttributesHashMap = BasicUtil.convertSchemaFieldListToHashMap(newAttributes);
			logger.info("Target Schema attributes : {}", newAttributes);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			
			List<String> existingAttributesNames = new ArrayList<>();
			for(int i=0; i<newAttributes.size(); i++) {

				SchemaFieldDTO fieldDto = newAttributes.get(i);
				
				boolean isPresent = false;
				for (Map<String, Object> field : schemaFields) {
					
					if(field.get("name").equals(fieldDto.getName())) {
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
				// REMOVE existing attributess from newAttributes list
				for(String attributeName: existingAttributesNames) {
					newAttributesHashMap.remove(attributeName);
				}
			}
			
			if(newAttributesHashMap.isEmpty()) {
				tableSchemaResponseDTO.setStatusCode(405);
				tableSchemaResponseDTO.setMessage("Add attributes operation NOT ALLOWED");
			} else {
				for(Map.Entry<String, SchemaFieldDTO> fieldDtoEntry: newAttributesHashMap.entrySet()) {
					SchemaFieldDTO fieldDto = fieldDtoEntry.getValue();
					if (!TableSchemaParser.validateSchemaField(fieldDto)) {
						logger.info("Validation failed for SolrFieldDTO before updating the current schema- {}", schemaName);
						tableSchemaResponseDTO.setStatusCode(400);
						break;
					}
					if (fieldDto.isSortable()) {
						fieldDto.setMultiValue(false); // For SortOnField UseCase MultiValue must be False
					}
					errorCausingField = fieldDto.getName();
					Map<String, Object> newField = new HashMap<>();
					newField.put("name", fieldDto.getName());
					newField.put("type", SchemaFieldType.fromStandardDataTypeToSolrFieldType(fieldDto.getType()));
					newField.put(REQUIRED, fieldDto.isRequired());
					newField.put(STORED, fieldDto.isStorable());
					newField.put(MULTIVALUED, fieldDto.isMultiValue());
					newField.put(INDEXED, fieldDto.isFilterable());
					newField.put(DOCVALUES, fieldDto.isSortable());
					SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
					addFieldResponse = addFieldRequest.process(solrClientActive);
					
					schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
				}
				tableSchemaResponseDTO.setStatusCode(200);
				tableSchemaResponseDTO.setMessage("New attributes are added successfully");
			}

		} catch (SolrServerException | IOException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.error(e.toString());
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table");
			logger.error(SOLR_ADD_ATTRIBUTES_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.info(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return tableSchemaResponseDTO;
	}


	@Override
	public ResponseDTO updateSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.info("Update Table Schema");

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL,
				newTableSchemaDTO.getTableName());
		
		ResponseDTO apiResponseDTO = new ResponseDTO();

		TableSchemaDTO schemaResponseDTOBefore = new TableSchemaDTO();
		TableSchemaDTO schemaResponseDTOAfter = new TableSchemaDTO();

		String errorCausingField = null;
		String payloadOperation = "";
		try {
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClientActive);
			
			schemaResponseDTOBefore.setStatusCode(200);

			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			logger.info("Total number of fields: {}", numOfFields);

			// Get all fields from incoming(from req Body) schemaDTO
			List<SchemaFieldDTO> newSchemaFields = newTableSchemaDTO.getAttributes();
			List<Map<String, Object>> targetSchemafields = TableSchemaParser
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);
			
			// Validate Target Schema Fields
			targetSchemafields = TableSchemaParser.validateTargetSchemaFields(targetSchemafields);
			
			// Check one validation -- useless
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if (validationEntry.containsKey(VALIDATED)) {
				Object validatedFields = validationEntry.get(VALIDATED);
				if (validatedFields.equals(false))
					throw new SolrSchemaValidationException("Target Schema Fields validation failed!");
			}

			int totalUpdatesRequired = newSchemaFields.size();

			// Update Schema Logic
			UpdateResponse updateFieldsResponse;
			NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			
			for (Map<String, Object> currField : targetSchemafields) {	
				errorCausingField = (String) currField.get("name");
				// Pass the fieldAttribute to be updated			
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solrClientActive);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				logger.info("Field- {} is successfully updated", currField.get("name"));
			}
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.info("Total field updates required in the current schema: {}", totalUpdatesRequired);
			logger.info("Total fields updated in the current schema: {}", updatedFields);

		} catch (SolrServerException | IOException e) {
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.error(e.toString());
		} catch (NullPointerException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error("Null value detected!", e);
			logger.error(e.toString());
		} catch (SolrException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error(e.getMessage(), " Existing schema fields couldn't be updated!");
			logger.error(e.toString());
		} catch (SolrSchemaValidationException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error("Error Message: {}", e.getMessage());
			logger.error(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	
	
	@Override
	public ResponseDTO addAliasTable(String tableOriginalName, String tableAlias) {
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(
					"Successfully renamed Solr Collection: " + tableOriginalName + " to " + tableAlias);
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO
					.setResponseMessage("Unable to rename Solr Collection: " + tableOriginalName + ". Exception.");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	
	
	@Override
	public ResponseDTO deleteConfigSet(String configSetName) {
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		configSetRequest.setMethod(METHOD.DELETE);
		configSetRequest.setConfigSetName(configSetName);
		try {
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClientActive);
			apiResponseDTO = new ResponseDTO(200, "ConfigSet got deleted successfully");
		} catch (Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be deleted");
			apiResponseDTO.setResponseStatusCode(401);
			logger.error("Error occured while deleting Config set. Exception: ", e);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}


	@Override
	public ResponseDTO initializeDeleteTableSchemaAttributes(
			TableSchemaDTO tableSchemaDTO, 
			int clientId, 
			String tableName) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		ResponseDTO deleteRecordInsertionResponse = new ResponseDTO();
		
		  File file=new File(deleteSchemaAttributesFilePath + ".txt");
		  if((clientId>0) && (tableName!=null)) {
		  try(FileWriter fw = new FileWriter(file, true);
		   BufferedWriter bw = new BufferedWriter(fw)) {
		      String newRecord = String.format("%d %18s %20s",clientId,tableName,formatter.format(Calendar.getInstance().getTime()))+"\n";
		      bw.write(newRecord);
		      logger.debug("Table {} Successfully Initialized for Deletion ",tableName);
		      deleteRecordInsertionResponse.setResponseStatusCode(200);
		      deleteRecordInsertionResponse.setResponseMessage("Table:" +tableName+" Successfully Initialized For Deletion ");
		  }catch(Exception e)
		  {
			  logger.error(ResponseMessages.TABLE_DELETE_INITIALIZE_ERROR_MSG ,tableName,e);
			  deleteRecordInsertionResponse.setResponseStatusCode(400);
			  deleteRecordInsertionResponse.setResponseMessage("Error While Initializing Deletion For Table: "+tableName);
		  }
		}else {
			  logger.debug(ResponseMessages.TABLE_DELETE_INITIALIZE_ERROR_MSG ,tableName);
			  deleteRecordInsertionResponse.setResponseStatusCode(400);
			  deleteRecordInsertionResponse.setResponseMessage("Invalid Client ID or Table Name Provided");
		}
		  return deleteRecordInsertionResponse;
	}
	
	
	// Table schema deletion
	public void checkForSchemaSoftDeletion(
			int clientId, 
			String tableName, 
			List<SchemaFieldDTO> newSchemaDTO) {
		
		List<SchemaFieldDTO> existingSchemaAttributes
			= getTableSchemaIfPresent(clientId, tableName).getAttributes();
		for(SchemaFieldDTO existingSchemaAttribute : existingSchemaAttributes) {
			String exsitingSchemaName = existingSchemaAttribute.getName();
			if(!(exsitingSchemaName.equalsIgnoreCase("_nest_path_")
					|| exsitingSchemaName.equalsIgnoreCase("_root_")
					|| exsitingSchemaName.equalsIgnoreCase("_text_") 
					|| exsitingSchemaName.equalsIgnoreCase("_version_") 
					|| exsitingSchemaName.equalsIgnoreCase("id"))
				&& !newSchemaDTO.contains(existingSchemaAttribute)) {
				initializeSchemaDeletion(clientId, tableName , existingSchemaAttribute.getName());
			}
		}
			
	}
	
	
	public void initializeSchemaDeletion(int clientId, String tableName,String columnName) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		  File file=new File("src\\main\\resources\\SchemaDeleteRecord.txt");
		  try(FileWriter fw = new FileWriter(file, true);
				   BufferedWriter bw = new BufferedWriter(fw)) {
			  String newRecord = String.format(
					  "%d %18s %20s %25s",
					  clientId,
					  tableName,
					  formatter.format(Calendar.getInstance().getTime()),columnName);
		      bw.write(newRecord);
		      bw.newLine();
		      logger.debug("Schema {} Succesfully Initialized For Deletion ",columnName);
		  } catch (IOException e) {
			logger.error("Error While Intializing Deletion for Schema :{} ",columnName);
		}
	}

	
	// Soft Delete Table Schema Info Retrieval
	public List<String> readSchemaInfoFromSchemaDeleteManager(
			int clientId, String tableName) {
		List<String> deletedSchemaAttributes = new ArrayList<>();
		
		File schemaSoftDeleteFile = new File(deleteSchemaAttributesFilePath + ".txt");
		try (BufferedReader br = new BufferedReader(new FileReader(schemaSoftDeleteFile))) {
			int lineNumber = 0;
			String currentDeleteRecordLine;
			while ((currentDeleteRecordLine = br.readLine()) != null) {
				if (lineNumber > 0) {
					String[] currentRecordData = currentDeleteRecordLine.split("\\s+");				
					if (currentRecordData[0].equalsIgnoreCase(String.valueOf(clientId))
							&&	currentRecordData[1].equalsIgnoreCase(String.valueOf(tableName))) {
						deletedSchemaAttributes.add(currentRecordData[4]);
					}
				}
				lineNumber++;
			}
		} catch (Exception e) {
			throw new OperationIncompleteException(500, "Soft Delete SchemaInfo could not be retrieved");
		}
		
		return deletedSchemaAttributes;
	}

	
	public void checkForSchemaDeletion() {
		File existingSchemaFile = new File("src\\main\\resources\\SchemaDeleteRecord.txt");
		File newSchemaFile = new File("src\\main\\resources\\SchemaDeleteRecordTemp.txt");
		int lineNumber = 0;
		int schemaDeleteRecordCount = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(existingSchemaFile));
				PrintWriter pw = new PrintWriter(new FileWriter(newSchemaFile))) {
			String currentSchemaDeleteRecord;
			while ((currentSchemaDeleteRecord = br.readLine()) != null) {
				if (lineNumber != 0) {
					long diff = checkDatesDifference(currentSchemaDeleteRecord);
					if (diff < 15) {
						pw.println(currentSchemaDeleteRecord);
					} else {
						if (performSchemaDeletion(currentSchemaDeleteRecord.split(" "))) {
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
			pw.close();
			br.close();
			makeDeleteTableFileChangesForDelete(newSchemaFile, existingSchemaFile, schemaDeleteRecordCount);
		} catch (IOException exception) {
			logger.error("Error While Performing Schema Deletion ", exception);
		}
	}
	 
	
	public long checkDatesDifference(String currentDeleteRecord) {
		try{
	    String[] data =  currentDeleteRecord.split(" ");
		StringBuilder date = new StringBuilder();
		int position = data.length - 2;
		for(int i = position ; i<data.length;i++) {
    		date.append( (i!= data.length -1) ? data[i] + " " : data[i] );
    	}
      Date requestDate = formatter.parse(date.toString());
      Date currentDate = formatter.parse(formatter.format(Calendar.getInstance().getTime()));
      long diffInMillies = Math.abs(requestDate.getTime() - currentDate.getTime());
	  return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		}catch(Exception e) {
			logger.error("Error!",e);
			return 0;
		}
	}
	
	
	public boolean performSchemaDeletion(String[] schemaDeleteData) {
		String columnName = schemaDeleteData[schemaDeleteData.length-1];
		String tableName = schemaDeleteData[schemaDeleteData.length-4];
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL,
				tableName);
		try {
			SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(columnName);
			UpdateResponse deleteFieldResponse = deleteFieldRequest.process(solrClientActive);
			if(deleteFieldResponse.getStatus() == 200) {
				logger.debug("Schema {} Succesfully Deleted ",columnName);
				return true;
			}else {
				logger.debug("Schema {} Deletion Failed ",columnName);
				return false;
			}	
		}catch(Exception e) {
			logger.error("Exception Occured While Performing Deletion for Schema {} "+columnName,e);
			return false;
		}
	}
	
	
	public void makeDeleteTableFileChangesForDelete(File newFile, File existingFile,int schemaDeleteRecordCount) {
		File schemaDeleteRecordFile = new File("src\\main\\resources\\SchemaDeleteRecord.txt");
		  if(existingFile.delete() && newFile.renameTo(schemaDeleteRecordFile )) {
		     checkTableDeletionStatus(schemaDeleteRecordCount);
		  }
	}
	
	
	public boolean checkTableDeletionStatus(int schemaDeleteRecordCount) {
		if(schemaDeleteRecordCount >0) {
        	logger.debug("Total Number of Schema's Found and Deleted: {}",schemaDeleteRecordCount);
        	return true;
	     }
	     else {
	      	logger.debug("No Schema Records Were Found and Deleted With Request More Or Equal To 15 days");
	      	return false;
	      }
	}
}
