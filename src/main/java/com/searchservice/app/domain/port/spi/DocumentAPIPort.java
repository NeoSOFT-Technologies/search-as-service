package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

public interface DocumentAPIPort {
	
	SolrClient getSolrClient(String urlString, String tableName);
	SolrClient getSolrCloudClient(String urlString, String tableName);
	CloudSolrClient getCloudSolrClient(String urlString, String tableName);
}