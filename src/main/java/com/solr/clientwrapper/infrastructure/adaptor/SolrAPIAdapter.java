package com.solr.clientwrapper.infrastructure.adaptor;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solr.clientwrapper.domain.port.spi.SolrAPIPort;

@Service
public class SolrAPIAdapter implements SolrAPIPort {

	private final Logger log = LoggerFactory.getLogger(SolrAPIAdapter.class);


	@Override
	public HttpSolrClient getSolrClient(String urlString) {
		log.debug("Getting Solr Client");
		
		return new HttpSolrClient.Builder(urlString).build();
	}
	
	@Override
	public HttpSolrClient getSolrClientWithTable(String urlString, String tableName) {
		log.debug("Getting Solr Client for given collection/table");
		
		return new HttpSolrClient.Builder(urlString+"/"+tableName).build();
	}
 
}
