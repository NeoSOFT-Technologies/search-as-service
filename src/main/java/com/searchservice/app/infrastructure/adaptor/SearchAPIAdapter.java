package com.searchservice.app.infrastructure.adaptor;

import com.searchservice.app.domain.port.spi.SearchAPIPort;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchAPIAdapter implements SearchAPIPort {
	private final Logger log = LoggerFactory.getLogger(SearchAPIAdapter.class);

	@Override
	public HttpSolrClient getSearchClient(String urlString) {
		log.debug("Getting Search Client");
		
		return new HttpSolrClient.Builder(urlString).build();
	}
	
	@Override
	public HttpSolrClient getSearchClientWithTable(String urlString, String tableName) {
		log.debug("Getting Search Client for given collection/table");
		
		return new HttpSolrClient.Builder(urlString+"/"+tableName).build();
	}
 
}
