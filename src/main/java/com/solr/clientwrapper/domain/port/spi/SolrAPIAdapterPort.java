package com.solr.clientwrapper.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

import com.solr.clientwrapper.domain.dto.solr.SolrAPIAdapterResponseDTO;

public interface SolrAPIAdapterPort {
	
	SolrClient getSolrClient(String urlString, String tableName);
	SolrClient getSolrCloudClient(String urlString, String tableName);
	CloudSolrClient getCloudSolrClient(String urlString, String tableName);
	SolrAPIAdapterResponseDTO getSolrClientAdapter(String urlString, String tableName);
}
