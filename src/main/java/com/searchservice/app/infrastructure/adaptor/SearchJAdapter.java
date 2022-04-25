package com.searchservice.app.infrastructure.adaptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.table.SchemaDto;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.domain.utils.SchemaFieldType;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.domain.utils.TableSchemaParserUtil;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

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

	@Value("${basic-auth.username}")
	private String basicAuthUsername;
	@Value("${basic-auth.password}")
	private String basicAuthPassword;
	
	@Autowired
	private SearchAPIAdapter searchAPIAdapter;
	
	
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
	
	
	public CollectionAdminResponse getTableDetailsFromSolrjCluster(HttpSolrClient searchClientActive) {

		CollectionAdminRequest.ClusterStatus clusterStatus = new CollectionAdminRequest.ClusterStatus();
		CollectionAdminResponse response = null;
		try {
			response = clusterStatus.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}

		return response;
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

	public void createConfigSetInSolrj(ConfigSetAdminRequest.Create configSetRequest,
			HttpSolrClient searchClientActive) {

		try {
			configSetRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.getMessage());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
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
			return fieldTypes.stream().anyMatch(ft -> ft.getAttributes().containsValue(SchemaDto.PARTIAL_SEARCH.getLabel()));
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURRED, e.getMessage());
			throw new BadRequestOccurredException(400, "Could not confirm if partial_search field type is available");
		}
	}
	
	
	public Map<String, Object> createPartialSearchFieldTypeIfNotPresent(TableSchema tableSchemaDTO) {
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
				throw new BadRequestOccurredException(400, "Couldn't create partial search field type");
			}
		} else
			fieldTypeAttributes.put("name", SchemaDto.PARTIAL_SEARCH.getLabel());
		
		return fieldTypeAttributes;
	}
	
	
	public List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(TableSchema tableSchemaDTO) {
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();

		for (SchemaField fieldDto : tableSchemaDTO.getColumns()) {
			Map<String, Object> fieldDtoMap = new HashMap<>();
			if (!TableSchemaParserUtil.validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(SchemaDto.VALIDATED.getLabel(), false);
				return schemaFieldsListOfMap;
			}
			if (TableSchemaParserUtil.isFieldUnchangeable(fieldDto.getName()))
				continue;

			// PARTIAL_SEARCH UPDATE
			partialSearchUpdate(tableSchemaDTO, fieldDto, fieldDtoMap);

			fieldDtoMap.put(SchemaDto.NAME.getLabel(), fieldDto.getName());
			fieldDtoMap.put(SchemaDto.STORED.getLabel(), fieldDto.isStorable());
			fieldDtoMap.put(SchemaDto.MULTIVALUED.getLabel(), fieldDto.isMultiValue());
			fieldDtoMap.put(SchemaDto.REQUIRED.getLabel(), fieldDto.isRequired());
			fieldDtoMap.put(SchemaDto.DOCVALUES.getLabel(), fieldDto.isSortable());
			fieldDtoMap.put(SchemaDto.INDEXED.getLabel(), fieldDto.isFilterable());
			schemaFieldsListOfMap.add(fieldDtoMap);
		}
		return schemaFieldsListOfMap;
	}
	
	
	public void partialSearchUpdate(TableSchema tableSchemaDTO, SchemaField fieldDto, Map<String, Object> fieldDtoMap) {
		// if partial search enabled
		if (fieldDto.isPartialSearch()) {
			// Add <partial-search> field-type if not present already
			Map<String, Object> fieldTypeAttributes = createPartialSearchFieldTypeIfNotPresent(tableSchemaDTO);

			// Add <partial-search> fieldType to the field
			fieldDtoMap.put("type", fieldTypeAttributes.get(SchemaDto.NAME.getLabel()));
			// Since "partial search" is enabled on this field, docValues has to be disabled
			fieldDto.setSortable(false);
		} else {
			fieldDtoMap.put("type", SchemaFieldType.fromStandardDataTypeToSearchFieldType(fieldDto.getType(),
					fieldDto.isMultiValue()));
			fieldDtoMap.put(SchemaDto.DOCVALUES.getLabel(), fieldDto.isSortable());
		}
	}
	
}