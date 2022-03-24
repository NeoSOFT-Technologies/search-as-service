package com.searchservice.app.infrastructure.adaptor;

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
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaRepresentation;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.searchservice.app.config.CapacityPlanProperties;
import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.ConfigSet;
import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.domain.utils.TypeCastingUtil;

@Service
public class SolrJAdapter {
	private static final String SEARCH_SCHEMA_EXCEPTION_MSG = "There's been an error in executing {} operation via schema API. "
			+ "Perhaps the target field- {} isn't present.";
	private final Logger logger = LoggerFactory.getLogger(SolrJAdapter.class);
	@Autowired
	SearchAPIAdapter searchAPIAdapter;
	HttpSolrClient searchClient;

	@Value("${base-search-url}")
	private String searchURL;

	@Value("${basic-auth.username}")
	private String basicAuthUsername;
	@Value("${basic-auth.password}")
	private String basicAuthPassword;

	public java.util.List<String> getCollectionAdminRequestList(int clientId) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		java.util.List<String> data = new ArrayList<String>();
		try {
			CollectionAdminResponse response = request.process(searchClientActive);

			data.addAll(TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"), clientId));

		} catch (Exception e) {
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return data;

	}

	public Map<Object, Object> getTableFetailsFromSolrjCluster(String tableName) {
		Map<Object, Object> finalResponseMap = new HashMap<>();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		CollectionAdminRequest.ClusterStatus clusterStatus = new CollectionAdminRequest.ClusterStatus();
		CollectionAdminResponse response = null;
		try {
			response = clusterStatus.process(searchClientActive);
			finalResponseMap = ManageTableUtil
					.getTableInfoFromClusterStatusResponseObject(response.getResponse().asMap(20), tableName);
		} catch (Exception e) {
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return finalResponseMap;
	}

	public Boolean deleteTableFromSolrj(String tableName) {
		CollectionAdminRequest.Delete request = CollectionAdminRequest.deleteCollection(tableName);
		CollectionAdminRequest.DeleteAlias deleteAliasRequest = CollectionAdminRequest.deleteAlias(tableName);
		HttpSolrClient searchClientActive = new HttpSolrClient.Builder(searchURL).build();
		try {
			request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			deleteAliasRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			request.process(searchClientActive);
			deleteAliasRequest.process(searchClientActive);
		} catch (Exception e) {
			logger.error("Exception occurred: ", e);
			return false;
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return true;
	}

	public java.util.List<String> getConfigSetFromSolrj() {
		java.util.List<String> data = new ArrayList<String>();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();
		try {
			ConfigSetAdminResponse configSetResponse = configSetRequest.process(searchClientActive);
			NamedList<Object> configResponseObjects = configSetResponse.getResponse();
			data = TypeCastingUtil.castToListOfStrings(configResponseObjects.get("configSets"));
		} catch (Exception e) {

		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return data;
	}

	public java.util.List<String> getAllTablesList() {
		java.util.List<String> allTables = new ArrayList<String>();
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		try {
			CollectionAdminResponse response = request.process(searchClientActive);
			allTables = TypeCastingUtil.castToListOfStrings(response.getResponse().get("collections"));
		} catch (Exception e) {
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return allTables;

	}

	public java.util.List<Map<String, Object>> getSchemaFields(String tableName) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
		SchemaRequest schemaRequest = new SchemaRequest();
		java.util.List<Map<String, Object>> schemaFields = new ArrayList<Map<String, Object>>();
		try {
			SchemaResponse schemaResponse = schemaRequest.process(searchClientActive);
			schemaFields = schemaResponse.getSchemaRepresentation().getFields();
		}

		catch (Exception e) {

		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return schemaFields;
	}

	public Boolean createConfigSetInSolrj(ConfigSet configSetDTO) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		ConfigSetAdminRequest.Create configSetRequest = new ConfigSetAdminRequest.Create();
		configSetRequest.setBaseConfigSetName(configSetDTO.getBaseConfigSetName());
		configSetRequest.setConfigSetName(configSetDTO.getConfigSetName());
		configSetRequest.setMethod(METHOD.POST);
		configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);

		try {
			configSetRequest.process(searchClientActive);
		} catch (SolrServerException e) {

			return false;
		} catch (IOException e) {

			return false;
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return true;
	}

	public Boolean createTableInSolrj(ManageTable manageTableDTO, CapacityPlanProperties.Plan selectedCapacityPlan) {
		CollectionAdminRequest.Create request = CollectionAdminRequest.createCollection(manageTableDTO.getTableName(),
				selectedCapacityPlan.getShards(), selectedCapacityPlan.getReplicas());
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);

		request.setMaxShardsPerNode(selectedCapacityPlan.getShards() * selectedCapacityPlan.getReplicas());
		request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
		try {
			request.process(searchClientActive);
		} catch (SolrServerException e) {

			return false;
		} catch (IOException e) {

			return false;
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return true;

	}

	public List<Map<String, Object>> addSchemaAttributesInSolrj(TableSchema newTableSchemaDTO) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		SchemaRequest schemaRequest = new SchemaRequest();
		schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
		List<Map<String, Object>> schemaFields = new ArrayList<Map<String, Object>>();

		SchemaResponse schemaResponse;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);
			SchemaRepresentation retrievedSchema = schemaResponse.getSchemaRepresentation();
			schemaFields = retrievedSchema.getFields();
		} catch (SolrServerException e) {

		} catch (IOException e) {

		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return schemaFields;
	}

	public void addFieldTypeRequest(Map<String, Object> fieldTypeAttributes, String tableName) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
		FieldTypeDefinition fieldTypeDef = new FieldTypeDefinition();
		fieldTypeDef.setAttributes(fieldTypeAttributes);
		SchemaRequest.AddFieldType addFieldTypeRequest = new SchemaRequest.AddFieldType(fieldTypeDef);
		try {
			addFieldTypeRequest.process(searchClientActive);
		} catch (SolrServerException e) {

		} catch (IOException e) {

		}
	}

	public void addFieldRequestInSolrj(Map<String, Object> newField, String fildName, String tableName) {
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
		SchemaRequest.AddField addFieldRequest = new SchemaRequest.AddField(newField);
		UpdateResponse addFieldResponse;
		try {
			addFieldResponse = addFieldRequest.process(searchClientActive);
			NamedList<Object> schemaResponseAddFields = new NamedList<>();
			schemaResponseAddFields.add(fildName, addFieldResponse.getResponse());
		} catch (SolrServerException e) {

		} catch (IOException e) {

		}
	}

	public List<Map<String, Object>> updateSchemaAttributesInSolrj(TableSchema newTableSchemaDTO) {

		SchemaRequest schemaRequest = new SchemaRequest();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		schemaRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);

		} catch (SolrServerException e) {

		} catch (IOException e) {

		}
		List<Map<String, Object>> schemaFields = schemaResponse.getSchemaRepresentation().getFields();
		return schemaFields;
	}

	public Response updateSchemaLogic(TableSchema newTableSchemaDTO, List<Map<String, Object>> targetSchemafields) {
		Response schemaResponseDTOAfter = new Response();
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
				newTableSchemaDTO.getTableName());
		String errorCausingField = null;
		String payloadOperation = "SchemaRequest.ReplaceField";
		int updatedFields = 0;

		UpdateResponse updateFieldsResponse;
		NamedList<Object> schemaResponseUpdateFields = new NamedList<>();
		try {
			for (Map<String, Object> currField : targetSchemafields) {
				errorCausingField = (String) currField.get("name");
				SchemaRequest.ReplaceField updateFieldsRequest = new SchemaRequest.ReplaceField(currField);
				updateFieldsResponse = updateFieldsRequest.process(searchClientActive);
				schemaResponseDTOAfter.setStatusCode(200);
				schemaResponseUpdateFields.add((String) currField.get("name"), updateFieldsResponse.getResponse());
				updatedFields++;
				logger.info("Field- {} is successfully updated", currField.get("name"));
			}
			logger.info("Total fields updated in the current schema: {}", +updatedFields);
		} catch (SolrServerException | IOException e) {
			schemaResponseDTOAfter.setStatusCode(400);
			logger.error(SEARCH_SCHEMA_EXCEPTION_MSG, payloadOperation, errorCausingField);
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return schemaResponseDTOAfter;

	}

	public Response addAliasTableInSolrj(String tableOriginalName, String tableAlias) {
		CollectionAdminRequest.Rename request = CollectionAdminRequest.renameCollection(tableOriginalName, tableAlias);
		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		request.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
		Response apiResponseDTO = new Response();
		try {
			request.process(searchClientActive);
			apiResponseDTO.setStatusCode(200);

		} catch (Exception e) {
			apiResponseDTO.setStatusCode(400);
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return apiResponseDTO;
	}

	public Boolean deleteConfigSetFromSolrj(String configSetName) {

		HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClient(searchURL);
		ConfigSetAdminRequest.Delete configSetRequest = new ConfigSetAdminRequest.Delete();
		configSetRequest.setMethod(METHOD.DELETE);
		configSetRequest.setConfigSetName(configSetName);

		try {
			configSetRequest.setBasicAuthCredentials(basicAuthUsername, basicAuthPassword);
			configSetRequest.process(searchClientActive);
		} catch (Exception e) {
			logger.error("Error occured while deleting Config set. Exception: ", e);
			return false;
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return true;
	}
public int performSchemaDeletion(String columnName, String tableName) {
	HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
	UpdateResponse deleteFieldResponse;
	int status =400;
	SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(columnName);
	try {
	 deleteFieldResponse = deleteFieldRequest.process(searchClientActive);
	 status = deleteFieldResponse.getStatus();
	}catch(Exception e) {
		logger.error("Exception Occured While Performing Deletion for Schema {} " + columnName, e);
	}
	return status;
}
public List<FieldTypeDefinition> isPartialSearchFieldInSolrj(String tableName){
	HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
	SchemaRequest schemaRequest = new SchemaRequest();
	List<FieldTypeDefinition> fieldTypes = null ;
	try {
	SchemaResponse schemaResponse = schemaRequest.process(searchClientActive);
	 fieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
	}
	catch (Exception e) {
		logger.debug("Schema Field Types couldn't be retrieved");
	}
	finally {
		SearchUtil.closeSearchClientConnection(searchClientActive);
	}
	return fieldTypes;
}
}
