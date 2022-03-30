package com.searchservice.app.infrastructure.adaptor;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.ConfigSetAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.CollectionAdminResponse;
import org.apache.solr.client.solrj.response.ConfigSetAdminResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.client.solrj.response.schema.SchemaResponse.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.searchservice.app.domain.utils.SearchUtil;

@Service
public class SolrJAdapter {

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

	public CollectionAdminResponse getCollectionAdminRequestList(HttpSolrClient searchClientActive) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		CollectionAdminResponse response = null;

		try {
			response = request.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return response;

	}

	public CollectionAdminResponse getTableDetailsFromSolrjCluster(String tableName,
			HttpSolrClient searchClientActive) {

		CollectionAdminRequest.ClusterStatus clusterStatus = new CollectionAdminRequest.ClusterStatus();
		CollectionAdminResponse response = null;
		try {
			response = clusterStatus.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
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
			logger.error("Exception occurred: ", e);
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
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return configSetResponse;
	}

	public CollectionAdminResponse getAllTablesList(HttpSolrClient searchClientActive) {
		CollectionAdminRequest.List request = new CollectionAdminRequest.List();
		CollectionAdminResponse response = null;
		try {
			response = request.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return response;

	}

	public SchemaResponse getSchemaFields(HttpSolrClient searchClientActive) {

		SchemaRequest schemaRequest = new SchemaRequest();
		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);

		}

		catch (SolrServerException | IOException e) {
			logger.error(e.toString());
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
			logger.error(e.toString());
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
	}

	public void createTableInSolrj(CollectionAdminRequest.Create request, HttpSolrClient searchClientActive) {

		try {
			request.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());

		} 

	}

	public SchemaResponse addSchemaAttributesInSolrj(HttpSolrClient searchClientActive, SchemaRequest schemaRequest) {

		SchemaResponse schemaResponse = null;
		try {
			schemaResponse = schemaRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		}

		return schemaResponse;
	}

	public void addFieldTypeRequest(SchemaRequest.AddFieldType addFieldTypeRequest, HttpSolrClient searchClientActive) {

		try {
			addFieldTypeRequest.process(searchClientActive);
		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		}
	}

	public UpdateResponse addFieldRequestInSolrj(SchemaRequest.AddField addFieldRequest,
			HttpSolrClient searchClientActive) {

		UpdateResponse addFieldResponse = null;
		try {
			addFieldResponse = addFieldRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		} 
		return addFieldResponse;
	}

//	public SchemaResponse updateSchemaAttributesInSolrj(HttpSolrClient searchClientActive,
//			SchemaRequest schemaRequest) {
//
//		SchemaResponse schemaResponse = null;
//
//		try {
//			schemaResponse = schemaRequest.process(searchClientActive);
//
//		} catch (SolrServerException | IOException e) {
//			logger.error(e.toString());
//		} finally {
//			SearchUtil.closeSearchClientConnection(searchClientActive);
//		}
//
//		return schemaResponse;
//	}

	public UpdateResponse updateSchemaLogic(HttpSolrClient searchClientActive,
			SchemaRequest.ReplaceField updateFieldsRequest) {

		UpdateResponse updateFieldsResponse = null;
		try {
			updateFieldsResponse = updateFieldsRequest.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
		}
		return updateFieldsResponse;

	}

	public void addAliasTableInSolrj(HttpSolrClient searchClientActive, CollectionAdminRequest.Rename request) {

		try {
			request.process(searchClientActive);

		} catch (SolrServerException | IOException e) {
			logger.error(e.toString());
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
		} finally {
			SearchUtil.closeSearchClientConnection(searchClientActive);
		}
		return schemaResponse;
	}
}