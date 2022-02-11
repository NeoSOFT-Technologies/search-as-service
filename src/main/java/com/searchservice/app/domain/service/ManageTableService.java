package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.Arrays;
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
import com.searchservice.app.domain.dto.ResponseDTO;
import com.searchservice.app.domain.dto.table.ConfigSetDTO;
import com.searchservice.app.domain.dto.table.GetCapacityPlanDTO;
import com.searchservice.app.domain.dto.table.ManageTableDTO;
import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;
import com.searchservice.app.domain.port.api.ManageTableServicePort;
import com.searchservice.app.domain.utils.SchemaFieldType;
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

	@Override
	public GetCapacityPlanDTO capacityPlans() {
		List<CapacityPlanProperties.Plan> capacityPlans = capacityPlanProperties.getPlans();
		return new GetCapacityPlanDTO(capacityPlans);

	}

	@Override
	public ResponseDTO isTablePresent(String tableName) {
		ResponseDTO apiResponseDTO = new ResponseDTO();
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		solrClient = new HttpSolrClient.Builder(solrURL).build();
		try {
			CollectionAdminResponse response = request.process(solrClient);
			List<String> allCollections = TypeCastingUtil
					.castToListOfStrings(response.getResponse().get("collections"));
			if (allCollections.contains(tableName)) {
				apiResponseDTO.setResponseStatusCode(200);
				apiResponseDTO.setResponseMessage("true");
			} else {
				apiResponseDTO.setResponseStatusCode(400);
				apiResponseDTO.setResponseMessage("false");
			}
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Error!");
		}
		return apiResponseDTO;
	}

	@Override
	public TableSchemaDTO getTableSchemaIfPresent(String tableName) {
		if (!isTableExists(tableName))
			throw new BadRequestOccurredException(400, String.format(TABLE_NOT_FOUND_MSG, tableName));
		return getTableSchema(tableName);
	}

	@Override
	public ResponseDTO getTables() {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		solrClient = solrAPIAdapter.getSolrClient(solrURL);

		ResponseDTO getListItemsResponseDTO = new ResponseDTO();
		try {
			CollectionAdminResponse response = request.process(solrClient);

			getListItemsResponseDTO
					.setItems(TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections")));
			getListItemsResponseDTO.setResponseStatusCode(200);
			getListItemsResponseDTO.setResponseMessage("Successfully retrieved all tables");

		} catch (Exception e) {
			logger.error(e.toString());
			getListItemsResponseDTO.setResponseStatusCode(400);
			getListItemsResponseDTO.setResponseMessage("Unable to retrieve tables");
		}
		return getListItemsResponseDTO;
	}

	@Override
	public ResponseDTO getConfigSets() {
		solrClient = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();

		ResponseDTO getListItemsResponseDTO = new ResponseDTO();
		try {
			ConfigSetAdminResponse configSetResponse = configSetRequest.process(solrClient);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			getListItemsResponseDTO
					.setItems(TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets")));
			getListItemsResponseDTO.setResponseStatusCode(200);
			getListItemsResponseDTO.setResponseMessage("Successfully retrieved all config sets");
		} catch (Exception e) {
			getListItemsResponseDTO.setResponseStatusCode(400);
			getListItemsResponseDTO.setResponseMessage("Configsets could not be retrieved. Error occured");
			logger.error("Error caused while retrieving configsets. Exception: ", e);
		}
		return getListItemsResponseDTO;
	}

	@Override
	public ResponseDTO createConfigSet(ConfigSetDTO configSetDTO) {
		solrClient = solrAPIAdapter.getSolrClient(solrURL);
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
			configSetRequest.process(solrClient);
			apiResponseDTO = new ResponseDTO(200, "ConfigSet is created successfully");
		} catch (Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be created");
			apiResponseDTO.setResponseStatusCode(400);
			logger.error("Error caused while creating ConfigSet. Exception: ", e);
		}
		return apiResponseDTO;
	}

	@Override
	public ResponseDTO createTableIfNotPresent(ManageTableDTO manageTableDTO) {
		if (isTableExists(manageTableDTO.getTableName()))
			throw new BadRequestOccurredException(400, manageTableDTO.getTableName() + " table already exists");

		if (!isConfigSetExists(manageTableDTO.getSchemaName())) {
			// Create Configset if not present
			logger.debug("{} configset is not present, creating..", manageTableDTO.getSchemaName());
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
			apiResponseDTO.setResponseStatusCode(tableSchemaResponseDTO.getStatusCode());
			apiResponseDTO.setResponseMessage(tableSchemaResponseDTO.getMessage());

		}
		return apiResponseDTO;
	}

	@Override
	public ResponseDTO deleteConfigSet(String configSetName) {
		solrClient = solrAPIAdapter.getSolrClient(solrURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		configSetRequest.setMethod(METHOD.DELETE);
		configSetRequest.setConfigSetName(configSetName);
		try {
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(solrClient);
			apiResponseDTO = new ResponseDTO(200, "ConfigSet got deleted successfully");
		} catch (Exception e) {
			apiResponseDTO.setResponseMessage("ConfigSet could not be deleted");
			apiResponseDTO.setResponseStatusCode(401);
			logger.error("Error occured while deleting Config set. Exception: ", e);
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
		solrClient = new HttpSolrClient.Builder(solrURL).build();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			deleteAliasRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClient);
			deleteAliasRequest.process(solrClient);

			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage("Table: " + tableName + ", is successfully deleted");
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Unable to delete table: " + tableName);
		}

		// Delete configSet attached to the table
		/*
		 * String configSetName = ""; if(apiResponseDTO.getResponseStatusCode()==200)
		 * apiResponseDTO = deleteConfigSet(configSetName);
		 */
		return apiResponseDTO;
	}

	@Override
	public ResponseDTO updateTableSchema(String tableName, TableSchemaDTO tableSchemaDTO) {
		tableSchemaDTO.setTableName(tableName);
		return updateSchemaAttributes(tableSchemaDTO);
	}

	@Override
	public ResponseDTO addAliasTable(String tableOriginalName, String tableAlias) {
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		solrClient = new HttpSolrClient.Builder(solrURL).build();

		ResponseDTO apiResponseDTO = new ResponseDTO();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClient);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(
					"Successfully renamed Solr Collection: " + tableOriginalName + " to " + tableAlias);
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO
					.setResponseMessage("Unable to rename Solr Collection: " + tableOriginalName + ". Exception.");
		}
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
	public boolean isTableExists(String tableName) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		solrClient = new HttpSolrClient.Builder(solrURL).build();
		try {
			CollectionAdminResponse response = request.process(solrClient);
			List<String> allTables = TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
			return allTables.contains(tableName);
		} catch (Exception e) {
			logger.error(e.toString());
			throw new BadRequestOccurredException(400, "Table Search operation could not be completed");
		}
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
		solrClient = new HttpSolrClient.Builder(solrURL).build();

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		try {
			logger.info("Going to process TABLE CREATE request!!");
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(solrClient);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage("Successfully created table: " + manageTableDTO.getTableName());
			;
		} catch (Exception e) {
			logger.error(e.toString());
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO
					.setResponseMessage("Unable to create table: " + manageTableDTO.getTableName() + ". Exception.");
		}
		return apiResponseDTO;
	}

	@Override
	public TableSchemaDTO addSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.debug("Add schema attributes");

		solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO();

		TableSchemaDTO schemaResponseDTOBefore = new TableSchemaDTO();
		TableSchemaDTO schemaResponseDTOAfter = new TableSchemaDTO();

		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "";
		try {
			// logic
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			schemaResponseDTOBefore.setStatusCode(200);

			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaName = retrievedSchema.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();

			// Add new fields present in the Target Schema to the given collection schema
			List<SchemaFieldDTO> newAttributes = newTableSchemaDTO.getAttributes();
			SchemaFieldDTO[] newSolrFieldDTOs = newAttributes.toArray(new SchemaFieldDTO[0]);
			logger.debug("\nTarget Schema fields : {}", (Object[]) newSolrFieldDTOs);
			// ####### Add Schema Fields logic #######
			UpdateResponse addFieldResponse;
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			payloadOperation = "SchemaRequest.AddField";
			boolean newFieldFound = false;
			for (SchemaFieldDTO fieldDto : newSolrFieldDTOs) {
				boolean isPresent = false;
				for (Map<String, Object> field : schemaFields) {
					if (field.containsKey(fieldDto.getName())) {
						isPresent = true;
						break;
					}
				}
				if (!isPresent)
					newFieldFound = true;
			}
			if (!newFieldFound) {
				schemaResponseDTOAfter.setStatusCode(400);
			}
			for (SchemaFieldDTO fieldDto : newSolrFieldDTOs) {
				if (!TableSchemaParser.validateSchemaField(fieldDto)) {
					logger.debug("Validate SolrFieldDTO before updating the current schema- {}", schemaName);
					schemaResponseDTOAfter.setStatusCode(400);
					break;
				}
				if (fieldDto.isSortable()) {
					fieldDto.setMultiValue(false); // For SortOnField UseCase MultiValue must be False

				}
				errorCausingField = fieldDto.getName();
				Map<String, Object> newField = new HashMap<>();
				newField.put("name", fieldDto.getName());
				newField.put("type", SchemaFieldType.fromObject(fieldDto.getType()));
				newField.put(REQUIRED, fieldDto.isRequired());
				newField.put(STORED, fieldDto.isStorable());
				newField.put(MULTIVALUED, fieldDto.isMultiValue());
				newField.put(INDEXED, fieldDto.isFilterable());
				newField.put(DOCVALUES, fieldDto.isSortable());
				SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
				addFieldResponse = addFieldRequest.process(solrClient);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseAddFields.add(fieldDto.getName(), addFieldResponse.getResponse());
			}
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is created successfully");
			logger.debug("Logging newly added fields' responses--");
			for (Object field : schemaResponseAddFields) {
				logger.debug("### Added Field Response : {}", field);
			}
		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
		} catch (SolrException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			tableSchemaResponseDTO.setStatusCode(400);
			tableSchemaResponseDTO.setMessage("Schema attributes could not be added to the table");
			logger.error(SOLR_EXCEPTION_MSG + " So schema fields can't be found/deleted!",
					newTableSchemaDTO.getTableName());
			logger.debug(e.toString());
		}
		return tableSchemaResponseDTO;
	}

	@Override
	public ResponseDTO updateSchemaAttributes(TableSchemaDTO newTableSchemaDTO) {
		logger.debug("Update Solr Schema");

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient solrClientUpdate = solrAPIAdapter.getSolrClientWithTable(solrURL,
				newTableSchemaDTO.getTableName());
		ResponseDTO apiResponseDTO = new ResponseDTO();

		TableSchemaDTO schemaResponseDTOBefore = new TableSchemaDTO();
		TableSchemaDTO schemaResponseDTOAfter = new TableSchemaDTO();

		String errorCausingField = null;
		String payloadOperation = "";
		try {
			schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			SchemaResponse schemaResponse = schemaRequest.process(solrClientUpdate);
			schemaResponseDTOBefore.setStatusCode(200);

			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			logger.debug("Total number of fields: {}", numOfFields);

			// Get all fields from incoming(from req Body) schemaDTO
			SchemaFieldDTO[] newSchemaFields = newTableSchemaDTO.getAttributes().toArray(new SchemaFieldDTO[0]);
			List<Map<String, Object>> targetSchemafields = TableSchemaParser
					.parseSchemaFieldDtosToListOfMaps(newTableSchemaDTO);
			// Validate Solr Schema Fields
			Map<String, Object> validationEntry = targetSchemafields.get(0);
			if (validationEntry.containsKey(VALIDATED)) {
				Object validatedFields = validationEntry.get(VALIDATED);
				if (validatedFields.equals(false))
					throw new SolrSchemaValidationException("Target Schema Fields validation falied!");
			}

			int totalUpdatesRequired = newSchemaFields.length;

			// Update Schema Logic
			UpdateResponse updateFieldsResponse;
			NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
			payloadOperation = "SchemaRequest.ReplaceField";
			int updatedFields = 0;
			for (Map<String, Object> currField : targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				// Pass all fieldAttributes to be updated
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(solrClientUpdate);
				schemaResponseDTOAfter.setStatusCode(200);

				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				logger.debug("Field- {} is successfully updated", currField.get("name"));
			}
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			// Compare required Vs Updated Fields
			logger.debug("Total field updates required in the current schema: {}", totalUpdatesRequired);
			logger.debug("Total fields updated in the current schema: {}", updatedFields);

		} catch (SolrServerException | IOException e) {
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
		} catch (NullPointerException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			apiResponseDTO.setResponseStatusCode(200);
			apiResponseDTO.setResponseMessage(SCHEMA_UPDATE_SUCCESS);
			logger.error("Null value detected!", e);
			logger.debug(e.toString());
		} catch (SolrException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error(SOLR_EXCEPTION_MSG + " So schema fields can't be found/deleted!",
					newTableSchemaDTO.getTableName());
			logger.debug(e.toString());
		} catch (SolrSchemaValidationException e) {
			apiResponseDTO.setResponseStatusCode(400);
			apiResponseDTO.setResponseMessage("Schema could not be updated");
			logger.error("Error Message: {}", e.getMessage());
			logger.debug(e.toString());
		}
		return apiResponseDTO;
	}

	@Override
	public TableSchemaDTO getTableSchema(String tableName) {
		logger.debug("Getting table schema");

		solrClient = solrAPIAdapter.getSolrClientWithTable(solrURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();

		TableSchemaDTO tableSchemaResponseDTO = new TableSchemaDTO();

		String schemaName = "";
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest";
		try {
			SchemaResponse schemaResponse = schemaRequest.process(solrClient);
			logger.debug("Get request has been processed. Setting status code = 200");
			tableSchemaResponseDTO.setStatusCode(200);

			SchemaRepresentation schemaRepresentation = schemaResponse.getSchemaRepresentation();
			schemaName = schemaRepresentation.getName();
			List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
			int numOfFields = schemaFields.size();
			SchemaFieldDTO[] solrSchemaFieldDTOs = new SchemaFieldDTO[numOfFields];
			logger.debug("Total number of fields: {}", numOfFields);

			int schemaFieldIdx = 0;
			for (Map<String, Object> f : schemaFields) {

				// Prepare the SolrFieldDTO
				SchemaFieldDTO solrFieldDTO = new SchemaFieldDTO();
				solrFieldDTO.setName((String) f.get("name"));

				// Parse Field Type Object(String) to Enum
				String fieldTypeObj = (String) f.get("type");
				String solrFieldType = SchemaFieldType.fromObject(fieldTypeObj);

				solrFieldDTO.setType(solrFieldType);
				TableSchemaParser.setFieldsToDefaults(solrFieldDTO);
				TableSchemaParser.setFieldsAsPerTheSchema(solrFieldDTO, f);
				solrSchemaFieldDTOs[schemaFieldIdx] = solrFieldDTO;
				schemaFieldIdx++;
			}
			logger.debug("Total fields stored in attributes array: {}", schemaFieldIdx);

			// prepare response dto
			tableSchemaResponseDTO.setSchemaName(schemaName);
			tableSchemaResponseDTO.setTableName(tableName);
			tableSchemaResponseDTO.setAttributes(Arrays.asList(solrSchemaFieldDTOs));
			tableSchemaResponseDTO.setStatusCode(200);
			tableSchemaResponseDTO.setMessage("Schema is retrieved successfully");
		} catch (SolrServerException | IOException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.debug(e.toString());
		} catch (SolrException e) {
			tableSchemaResponseDTO.setStatusCode(400);
			logger.error(SOLR_EXCEPTION_MSG, tableName);
			logger.debug(e.toString());
		}
		return tableSchemaResponseDTO;
	}

	@Override
	public Map<Object, Object> getTableDetails(String tableName) {
		solrClient = solrAPIAdapter.getSolrClient(solrURL);

		Map<Object, Object> finalResponseMap = new HashMap<>();

		CollectionAdminRequest.ClusterStatus clusterStatus = new CollectionAdminRequest.ClusterStatus();

		CollectionAdminResponse response = null;

		try {
			response = clusterStatus.process(solrClient);
		} catch (Exception e) {
			logger.error(e.toString());
			finalResponseMap.put("Error", "Error connecting to cluster.");
			return finalResponseMap;
		}

		Map<Object, Object> responseAsMap = response.getResponse().asMap(20);
		Map<Object, Object> clusterResponse = (Map<Object, Object>) responseAsMap.get("cluster");
		Map<Object, Object> collections = (Map<Object, Object>) clusterResponse.get("collections");

		if (collections.containsKey(tableName)) {
			finalResponseMap = (Map<Object, Object>) collections.get(tableName);
		} else {
			finalResponseMap.put("Error", "Invalid table name.");
			return finalResponseMap;
		}

		return finalResponseMap;
	}
}
