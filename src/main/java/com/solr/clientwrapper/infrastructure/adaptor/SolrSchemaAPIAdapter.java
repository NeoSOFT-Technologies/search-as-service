package com.solr.clientwrapper.infrastructure.adaptor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solr.clientwrapper.domain.port.spi.SolrSchemaAPIPort;
import com.solr.clientwrapper.domain.service.SolrSchemaService;

@Service
public class SolrSchemaAPIAdapter implements SolrSchemaAPIPort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaAPIAdapter.class);

	@Override
	public SolrClient getSolrClient(String urlString, String tableName) {
		log.debug("Getting Solr Client for collection/table: {}", tableName);
		
		return new HttpSolrClient.Builder(urlString+tableName).build();
	}

	@Override
	public SolrClient getSolrCloudClient(String urlString, String tableName) {
		log.debug("Getting Solr Cloud Client for collection/table: {}", tableName);
		
		// Using already running Solr nodes
		return new CloudSolrClient.Builder().withSolrUrl(urlString).build();
	}
	
	@Override
	public CloudSolrClient getCloudSolrClient(String urlString, String tableName) {
		log.debug("Getting Cloud Solr Client for collection/table: {}", tableName);
		
		// Using already running Solr nodes
		return new CloudSolrClient.Builder().withSolrUrl(urlString).build();
	}
 
}