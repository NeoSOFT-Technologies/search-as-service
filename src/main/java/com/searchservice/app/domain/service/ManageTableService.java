package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.searchservice.app.domain.utils.SolrUtil;
import com.searchservice.app.domain.utils.TableSchemaParser;
import com.searchservice.app.domain.utils.TypeCastingUtil;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.ContentNotFoundException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;
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
    private static final String DEFAULT_CONFIGSET = "_default";
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
	
	private String servicename = "Manage_Table_Service";
	
	private String username = "Username";

	@Autowired
	CapacityPlanProperties capacityPlanProperties;

	@Autowired
	SolrAPIAdapter solrAPIAdapter;
	HttpSolrClient solrClient;

	public ManageTableService(String solrUrl, SolrAPIAdapter solrAPIAdapter, HttpSolrClient solrClient) {
		this.solrURL = solrUrl;
		this.solrAPIAdapter = solrAPIAdapter;
		this.solrClient = solrClient;
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
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		
		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        LoggerUtils.printlogger(loggersDTO,false,false);
		return new GetCapacityPlan(capacityPlans);
	}

	
	@Override
	public Response getTables(int clientId,LoggersDTO loggersDTO) {
		
		logger.debug("get Tables");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);

		Response getListItemsResponseDTO = new Response();

		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        
		try {
			CollectionAdminResponse response = request.process(solrClientActive);

			getListItemsResponseDTO
					.setData(TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"),clientId));
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all tables");

			LoggerUtils.printlogger(loggersDTO,false,false);

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setStatusCode(400);
			getListItemsResponseDTO.setMessage("Unable to retrieve tables");

			LoggerUtils.printlogger(loggersDTO,false,true);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return getListItemsResponseDTO;
	}
	

	@Override
	public TableSchemav2 getTableSchemaIfPresent(String tableName,LoggersDTO loggersDTO) {

		logger.debug("get Table Schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);

		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        
		if (!isTableExists(tableName))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName.split("_")[0]));
		TableSchemav2 tableSchema = getTableSchema(tableName); 
		LoggerUtils.printlogger(loggersDTO,false,false);
		return  tableSchema;

		
		
	}
	
	
	@Override
	public Map<Object, Object> getTableDetails(String tableName,LoggersDTO loggersDTO) {

		logger.debug("get Table Details");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
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

		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        
		if(!finalResponseMap.containsKey("tableDetails")
				|| finalResponseMap.get("tableDetails") == null) {
			finalResponseMap = new HashMap<>();
			finalResponseMap.put("Error", "Invalid table name provided.");
			LoggerUtils.printlogger(loggersDTO,false,true);
			return finalResponseMap;
		}
		else {

            LoggerUtils.printlogger(loggersDTO,false,false);
			return finalResponseMap;
		}
	}


	@Override
	public Response createTableIfNotPresent(ManageTable manageTableDTO,LoggersDTO loggersDTO) {

		logger.debug("create Table If Not Present");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		if (isTableExists(manageTableDTO.getTableName()))
			throw new BadRequestOccurredException(400, manageTableDTO.getTableName() + " table already exists");

		// Configset is present, proceed
		Response apiResponseDTO = createTable(manageTableDTO);


		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);

		if (apiResponseDTO.getStatusCode() == 200) {
			// Add schemaAttributes
			TableSchema tableSchemaDTO = new TableSchema(manageTableDTO.getTableName(),
			        DEFAULT_CONFIGSET, manageTableDTO.getColumns());
			Response tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);
			logger.info("Adding schema attributes response: {}", tableSchemaResponseDTO.getMessage());

            LoggerUtils.printlogger(loggersDTO,false,false);
		}
		else if(apiResponseDTO.getStatusCode() == 400) {
			LoggerUtils.printlogger(loggersDTO,false,true);
		}
		return apiResponseDTO;
	}


	@Override
	public Response deleteTable(String tableName,LoggersDTO loggersDTO) {

		logger.debug("delete Table");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		if (!isTableExists(tableName))
			throw new ContentNotFoundException(404, String.format(TABLE_NOT_FOUND_MSG, tableName.split("_")[0]));

		// Delete table
		CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(tableName);
		CollectionAdminRequest.DeleteAlias deleteAliasRequest = CollectionAdminRequest.deleteAlias(tableName);
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		Response apiResponseDTO = new Response();

		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			deleteAliasRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			deleteAliasRequest.process(solrClientActive);

			apiResponseDTO.setStatusCode(200);
			LoggerUtils.printlogger(loggersDTO,false,false);
			apiResponseDTO.setMessage("Table: " + tableName + ", is successfully deleted");

            
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			apiResponseDTO.setStatusCode(400);
			LoggerUtils.printlogger(loggersDTO,false,true);
			apiResponseDTO.setMessage("Unable to delete table: " + tableName);

			
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}

	
	@Override
	public Response updateTableSchema(String tableName, TableSchema tableSchemaDTO,LoggersDTO loggersDTO) {

		logger.debug("update Table Schema");
		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);
		
		tableSchemaDTO.setTableName(tableName);
		Response apiResponseDTO = new Response();
		
		// ADD new schema fields to the table

		Response tableSchemaResponseDTO = addSchemaAttributes(tableSchemaDTO);

		apiResponseDTO.setStatusCode(tableSchemaResponseDTO.getStatusCode());
		apiResponseDTO.setMessage(tableSchemaResponseDTO.getMessage());
		logger.info("New attributes addition response: {}", apiResponseDTO.getMessage());
		
		// UPDATE existing schema attributes
		apiResponseDTO = updateSchemaAttributes(tableSchemaDTO);
		logger.info("Existing attributes update response: {}", apiResponseDTO.getMessage());
		
		String timestamp=LoggerUtils.utcTime().toString();
        loggersDTO.setTimestamp(timestamp);
        
        LoggerUtils.printlogger(loggersDTO,false,false);
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
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();

		Response getListItemsResponseDTO = new Response();
		try {
			ConfigSetAdminResponse configSetResponse = configSetRequest.process(solrClientActive);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			getListItemsResponseDTO
					.setData(TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets")));
			getListItemsResponseDTO.setStatusCode(200);
			getListItemsResponseDTO.setMessage("Successfully retrieved all config sets");
		} catch (Exception e) {
			getListItemsResponseDTO.setStatusCode(400);
			getListItemsResponseDTO.setMessage("Configsets could not be retrieved. Error occured");
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
	public TableSchemav2 getTableSchema(String tableName) {
		logger.info("Getting table schema");

		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		TableSchemav2 tableSchemaResponseDTO = new TableSchemav2();
		TableSchemav2Data data= new TableSchemav2Data();
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
			List<SchemaField> solrSchemaFieldDTOs = new ArrayList<>();
			logger.info("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {
				
				// Prepare the SolrFieldDTO
				SchemaField solrFieldDTO = new SchemaField();
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
			data.setTableName(tableName.split("_")[0]);
			data.setColumns(solrSchemaFieldDTOs);
			tableSchemaResponseDTO.setData(data);
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
	public Response createConfigSet(ConfigSet configSetDTO) {
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Create configSetRequest = new ConfigSetAdminRequest.Create();
		Response apiResponseDTO = new Response();

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
			apiResponseDTO = new Response(200, "ConfigSet is created successfully");
		} catch (Exception e) {
			apiResponseDTO.setMessage("ConfigSet could not be created");
			apiResponseDTO.setStatusCode(400);
			logger.error("Error caused while creating ConfigSet. Exception: ", e);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
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
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Invalid SKU: " + manageTableDTO.getSku());
			return apiResponseDTO;
		}

		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(manageTableDTO.getTableName(),
				DEFAULT_CONFIGSET, selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage("Successfully created table: " + manageTableDTO.getTableName());
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO
					.setMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}

	
	@Override
	public Response addSchemaAttributes(TableSchema newTableSchemaDTO) {
		logger.info("Add schema attributes");

		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL, newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		Response tableSchemaResponseDTO = new Response();

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
			List<SchemaField> newAttributes = newTableSchemaDTO.getColumns();
			HashMap<String, SchemaField> newAttributesHashMap = BasicUtil.convertSchemaFieldListToHashMap(newAttributes);
			logger.info("Target Schema attributes : {}", newAttributes);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			
			List<String> existingAttributesNames = new ArrayList<>();
			for(int i=0; i<newAttributes.size(); i++) {

				SchemaField fieldDto = newAttributes.get(i);
				
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
				for(Map.Entry<String, SchemaField> fieldDtoEntry: newAttributesHashMap.entrySet()) {
					SchemaField fieldDto = fieldDtoEntry.getValue();
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
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table "+e.getMessage());
			
			logger.error(SOLR_ADD_ATTRIBUTES_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.info(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return tableSchemaResponseDTO;
	}


	@Override
	public Response updateSchemaAttributes(TableSchema newTableSchemaDTO) {
		logger.info("Update Table Schema");

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClientWithTable(solrURL,
				newTableSchemaDTO.getTableName());
		
		Response apiResponseDTO = new Response();

		Response schemaResponseDTOBefore = new Response();
		Response schemaResponseDTOAfter = new Response();

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
			UpdateResponse updateFieldsResponse;
			NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			for (Map<String, Object> currField : targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				// Pass the fieldAttribute to be updated
				
//				tempDelete
				// delete
				
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solrClientActive);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				logger.info("Field- {} is successfully updated", currField.get("name"));
			}
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.info("Total field updates required in the current schema: {}", totalUpdatesRequired);
			logger.info("Total fields updated in the current schema: {}", updatedFields);

		} catch (SolrServerException | IOException e) {
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.error(e.toString());
		} catch (NullPointerException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error("Null value detected!", e);
			logger.error(e.toString());
		} catch (SolrException e) {
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error(SOLR_EXCEPTION_MSG + " Existing schema fields couldn't be updated!",
					newTableSchemaDTO.getTableName());
			logger.error(e.toString());
		} catch (SolrSchemaValidationException e) {
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO.setMessage("Schema could not be updated");
			logger.error("Error Message: {}", e.getMessage());
			logger.error(e.toString());
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	
	
	
	@Override
	public Response addAliasTable(String tableOriginalName, String tableAlias) {
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		HttpSolrClient solrClientActive = new HttpSolrClient.Builder(solrURL).build();

		Response apiResponseDTO = new Response();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClientActive);
			apiResponseDTO.setStatusCode(200);
			apiResponseDTO.setMessage(
					"Successfully renamed Solr Collection: " + tableOriginalName + " to " + tableAlias);
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setStatusCode(400);
			apiResponseDTO
					.setMessage("Unable to rename Solr Collection: " + tableOriginalName + ". Exception.");
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	
	
	@Override
	public Response deleteConfigSet(String configSetName) {
		HttpSolrClient solrClientActive = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();

		Response apiResponseDTO = new Response();
		configSetRequest.setMethod(METHOD.DELETE);
		configSetRequest.setConfigSetName(configSetName);
		try {
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClientActive);
			apiResponseDTO = new Response(200, "ConfigSet got deleted successfully");
		} catch (Exception e) {
			apiResponseDTO.setMessage("ConfigSet could not be deleted");
			apiResponseDTO.setStatusCode(401);
			logger.error("Error occured while deleting Config set. Exception: ", e);
		} finally {
			SolrUtil.closeSolrClientConnection(solrClientActive);
		}
		return apiResponseDTO;
	}
	 
}
