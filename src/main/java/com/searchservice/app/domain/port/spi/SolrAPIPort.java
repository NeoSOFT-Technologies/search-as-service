package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

public interface SolrAPIPort {
	HttpSolrClient getSolrClient(String urlString);
	SolrClient getSolrClientWithTable(String urlString, String tableName);
}
