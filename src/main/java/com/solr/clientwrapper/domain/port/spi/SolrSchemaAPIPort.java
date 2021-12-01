package com.solr.clientwrapper.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;

public interface SolrSchemaAPIPort {
	
	SolrClient getSolrClient(String tableName);
}
