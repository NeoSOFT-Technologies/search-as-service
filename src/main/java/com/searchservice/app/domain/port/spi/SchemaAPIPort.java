package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

public interface SchemaAPIPort {
	
	SolrClient getSearchClient(String urlString, String tableName);
	SolrClient getSearchCloudClient(String urlString, String tableName);
	CloudSolrClient getCloudSearchClient(String urlString, String tableName);
}
