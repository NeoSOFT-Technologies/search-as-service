package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.impl.HttpSolrClient;

public interface SearchAPIPort {
	HttpSolrClient getSearchClient(String urlString);
	HttpSolrClient getSearchClientWithTable(String urlString, String tableName);
}
