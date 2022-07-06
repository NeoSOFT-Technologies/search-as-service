package com.searchservice.app.infrastructure.adaptor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.dto.table.ManageTable;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.SchemaLabel;
import com.searchservice.app.domain.utils.ManageTableUtil;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.domain.utils.TableSchemaParserUtil;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@NoArgsConstructor
public class SearchJAdapter {

	private static final String EXCEPTION_OCCURRED = "Exception occurred: {}";
	
	private final Logger logger = LoggerFactory.getLogger(SearchJAdapter.class);
	
	@Value("${base-search-url}")
	private String searchURL;
	
	@Value("${manage-table.config-overlay-url}")
	private String configOverlayUrl;

	@Value("${basic-auth.username}")
	private String basicAuthUsername;
	@Value("${basic-auth.password}")
	private String basicAuthPassword;
	
	@Autowired
	private SearchAPIAdapter searchAPIAdapter;
	
	@Autowired
	RestTemplate restTemplate;
	
	
	public CollectionAdminResponse getCollectionAdminRequestList(HttpSolrClient searchClientActive) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		CollectionAdminResponse response = null;

		try {
			response = request.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return response;

	}
	
	public String getClusterStatusFromSolrjCluster(HttpSolrClient searchClientActive) {

		ResponseEntity<String> response = null;
		String clusterStatusResponseString = null;
		try {
			String url = searchURL+ "/admin/collections?action=CLUSTERSTATUS";
			response = restTemplate.getForEntity(new URI(url), String.class);
			
			clusterStatusResponseString = response.getBody();

		} catch (Exception e) {
			logger.error("Exception occurred while fetching cluster status: ", e);
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return clusterStatusResponseString;
	}
	
	public Map<String, String> getUserPropsFromCollectionConfig(String tableNameWithTenantId) {
		
		ResponseEntity<String> response = null;
		Map<String, String> userPropsResponseMap = null;
		try {
			String url = searchURL+ "/" +tableNameWithTenantId+ configOverlayUrl;
			response = restTemplate.getForEntity(new URI(url), String.class);

			userPropsResponseMap = ManageTableUtil.getUserPropsFromJsonResponse(response.getBody());
		} catch (HttpClientErrorException e) {
			logger.debug("User properties from config overlay could not be fetched: {}", e.getMessage());
			return Collections.emptyMap();
		} catch (Exception e) {
			logger.error("Exception occurred while fetching user properties from config overlay: ", e);
			return Collections.emptyMap();
		}

		return userPropsResponseMap;
	}
	
	public void setUserPropertiesInCollectionConfig(Map<String, String> propsMap, String tableNameWithTenantId) {
		
		String setUserPropsUrl = searchURL + "/" +tableNameWithTenantId+ "/config";
		JSONObject setPropsJson = new JSONObject();
		JSONObject jsonProp = new JSONObject();
		
		// Prepare RequestBodyJson
		Iterator<String> itr = propsMap.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			jsonProp.put(key, propsMap.get(key));
		}
		setPropsJson.put("set-user-property", jsonProp);

		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<String> request = new HttpEntity<>(setPropsJson.toString(), headers);
	    String responseString = "";
	    try {
	    	responseString = restTemplate.postForObject(setUserPropsUrl, request, String.class);
		} catch (Exception e) {
			logger.error("Something Went Wrong While Setting User Properties", e);
			throw new CustomException(
					HttpStatusCode.SAAS_SERVER_ERROR.getCode(), 
					HttpStatusCode.SAAS_SERVER_ERROR, 
					"User Properties could not be set. "+HttpStatusCode.SAAS_SERVER_ERROR.getMessage());
		}
	    
	    // Validate response
		JSONObject responseObj = new JSONObject(responseString);
		JSONObject responseHeaderObject = responseObj.getJSONObject("responseHeader");
		int status = Integer.parseInt(responseHeaderObject.get("status").toString());
		if(status != 0)
			throw new CustomException(
					HttpStatusCode.SAAS_SERVER_ERROR.getCode(), 
					HttpStatusCode.SAAS_SERVER_ERROR, 
					"User Properties could not be set. "+HttpStatusCode.SAAS_SERVER_ERROR.getMessage());
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
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return true;
	}
	
	public ConfigSetAdminResponse getConfigSetFromSolrj(HttpSolrClient searchClientActive) {

		ConfigSetAdminRequest.List configSetRequest = new ConfigSetAdminRequest.List();
		ConfigSetAdminResponse configSetResponse = null;
		try {
			configSetResponse = configSetRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return configSetResponse;
	}

	public SchemaResponse getSchemaFields(HttpSolrClient searchClientActive) {

		SchemaRequest schemaRequest = new SchemaRequest();
		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);

		}

		catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return schemaResponse;
	}

	public void createTableInSolrj(CollectionAdminRequest.Create request, HttpSolrClient searchClientActive) {

		try {
			request.process(searchClientActive);			
			
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());

		} 

	}

	public SchemaResponse processSchemaRequest(HttpSolrClient searchClientActive, SchemaRequest schemaRequest) {

		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}

		return schemaResponse;
	}

	public void addFieldTypeRequest(SchemaRequest.AddFieldType addFieldTypeRequest, HttpSolrClient searchClientActive) {

		try {
			addFieldTypeRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
	}

	public UpdateResponse addFieldRequestInSolrj(SchemaRequest.AddField addFieldRequest,
			HttpSolrClient searchClientActive) {

		UpdateResponse addFieldResponse = null;
		try {
			addFieldResponse = addFieldRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} 
		return addFieldResponse;
	}

	public UpdateResponse updateSchemaLogic(HttpSolrClient searchClientActive,
			SchemaRequest.ReplaceField updateFieldsRequest) {

		UpdateResponse updateFieldsResponse = null;
		try {
			updateFieldsResponse = updateFieldsRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		}
		return updateFieldsResponse;

	}

	public void addAliasTableInSolrj(HttpSolrClient searchClientActive, CollectionAdminRequest.Rename request) {

		try {
			request.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

	}

	public void deleteConfigSetFromSolrj(HttpSolrClient searchClientActive,
			ConfigSetAdminRequest.Delete configSetRequest) {

		try {

			configSetRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error("Error occured while deleting Config set. Exception: ", e);

		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
	}

	public UpdateResponse performSchemaDeletion(HttpSolrClient searchClientActive,
			SchemaRequest.DeleteField deleteFieldRequest) {

		UpdateResponse deleteFieldResponse = null;

		try {
			deleteFieldResponse = deleteFieldRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error("Exception Occured While Performing Deletion for Schema ", e);
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return deleteFieldResponse;
	}

	public SchemaResponse isPartialSearchFieldInSolrj(HttpSolrClient searchClientActive, SchemaRequest schemaRequest) {

		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.debug("Schema Field Types couldn't be retrieved");
			logger.debug(EXCEPTION_OCCURRED, e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return schemaResponse;
	}
	
	public boolean isPartialSearchFieldTypePresent(String tableName) {
		try {
			HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL, tableName);
			SchemaRequest schemaRequest = new SchemaRequest();

			SchemaResponse schemaResponse = isPartialSearchFieldInSolrj(searchClientActive, schemaRequest);
			List<FieldTypeDefinition> fieldTypes = schemaResponse.getSchemaRepresentation().getFieldTypes();
			return fieldTypes.stream().anyMatch(ft -> ft.getAttributes().containsValue(SchemaLabel.PARTIAL_SEARCH.getLabel()));
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURRED, e.getMessage());
			throw new CustomException(400,HttpStatusCode.BAD_REQUEST_EXCEPTION,"Could not confirm if partial_search field type is available");
		}
	}
	
	public Map<String, Object> createPartialSearchFieldTypeIfNotPresent(ManageTable tableSchemaDTO) {
		Map<String, Object> fieldTypeAttributes = TableSchemaParserUtil.partialSearchFieldTypeAttrs;
		if (!isPartialSearchFieldTypePresent(tableSchemaDTO.getTableName())) {
			FieldTypeDefinition fieldTypeDef = new FieldTypeDefinition();
			fieldTypeDef.setAttributes(fieldTypeAttributes);
			
			try {
				SchemaRequest.AddFieldType addFieldTypeRequest = new SchemaRequest.AddFieldType(
						fieldTypeDef);
				HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
						tableSchemaDTO.getTableName());
				addFieldTypeRequest(addFieldTypeRequest, searchClientActive);
			} catch(Exception e) {
				logger.error(EXCEPTION_OCCURRED, e.getMessage());
				throw new CustomException(400,HttpStatusCode.BAD_REQUEST_EXCEPTION,"Couldn't create partial search field type");
			}
		} else
			fieldTypeAttributes.put("name", SchemaLabel.PARTIAL_SEARCH.getLabel());
		
		return fieldTypeAttributes;
	}

	public List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(ManageTable tableSchemaDTO) {
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();

		for (SchemaField fieldDto : tableSchemaDTO.getColumns()) {
			Map<String, Object> fieldDtoMap = new HashMap<>();
			if (!TableSchemaParserUtil.validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(SchemaLabel.VALIDATED.getLabel(), false);
				return schemaFieldsListOfMap;
			}
			if (TableSchemaParserUtil.isFieldUnchangeable(fieldDto.getName()))
				continue;

			// PARTIAL_SEARCH UPDATE
			partialSearchUpdate(tableSchemaDTO, fieldDto, fieldDtoMap);

			fieldDtoMap.put(SchemaLabel.NAME.getLabel(), fieldDto.getName());
			fieldDtoMap.put(SchemaLabel.STORED.getLabel(), fieldDto.isStorable());
			fieldDtoMap.put(SchemaLabel.MULTIVALUED.getLabel(), fieldDto.isMultiValue());
			fieldDtoMap.put(SchemaLabel.REQUIRED.getLabel(), fieldDto.isRequired());
			fieldDtoMap.put(SchemaLabel.DOCVALUES.getLabel(), fieldDto.isSortable());
			fieldDtoMap.put(SchemaLabel.INDEXED.getLabel(), fieldDto.isFilterable());
			schemaFieldsListOfMap.add(fieldDtoMap);
		}
		return schemaFieldsListOfMap;
	}

	public void partialSearchUpdate(ManageTable tableSchemaDTO, SchemaField fieldDto, Map<String, Object> fieldDtoMap) {
		// if partial search enabled
		if (fieldDto.isPartialSearch()) {
			// Add <partial-search> field-type if not present already
			Map<String, Object> fieldTypeAttributes = createPartialSearchFieldTypeIfNotPresent(tableSchemaDTO);

			// Add <partial-search> fieldType to the field
			fieldDtoMap.put("type", fieldTypeAttributes.get(SchemaLabel.NAME.getLabel()));
			// Since "partial search" is enabled on this field, docValues has to be disabled
			fieldDto.setSortable(false);
		} else {
			fieldDtoMap.put("type", SchemaFieldType.fromStandardDataTypeToSearchFieldType(fieldDto.getType(),
					fieldDto.isMultiValue()));
			fieldDtoMap.put(SchemaLabel.DOCVALUES.getLabel(), fieldDto.isSortable());
		}
	}
	
	// Utility methods
	public boolean checkIfSearchServerDown() {
		try {
			URL url = new URL(searchURL);
			
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
			httpURLConnection.connect();
			
		} catch (Exception e) {		//possible cause: MalformedURLException
			throw new CustomException(
					HttpStatusCode.CONNECTION_REFUSED.getCode(), 
					HttpStatusCode.CONNECTION_REFUSED, 
					HttpStatusCode.CONNECTION_REFUSED.getMessage());
		}
		
		return false;
	}
}