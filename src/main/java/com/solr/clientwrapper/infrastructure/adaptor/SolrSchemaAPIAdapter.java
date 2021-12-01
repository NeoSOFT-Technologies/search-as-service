package com.solr.clientwrapper.infrastructure.adaptor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.solr.clientwrapper.domain.port.spi.SolrSchemaAPIPort;
import com.solr.clientwrapper.domain.service.SolrSchemaService;

public class SolrSchemaAPIAdapter implements SolrSchemaAPIPort {

	private final Logger log = LoggerFactory.getLogger(SolrSchemaService.class);
	
	private static String DEFAULT_COLLECTION = "techproducts";
	String urlString = "http://localhost:8985/solr/";

	@Override
	public SolrClient getSolrClient(String tableName) {
		log.debug("Getting Solr Client for collection/table: {}", tableName);
		SolrClient solr = new HttpSolrClient.Builder(urlString+tableName).build();
		return solr;
	}
}
