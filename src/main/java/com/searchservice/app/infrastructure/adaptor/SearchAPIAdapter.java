package com.searchservice.app.infrastructure.adaptor;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.port.spi.SearchAPIPort;

@Service
public class SearchAPIAdapter implements SearchAPIPort {
	

	@Override
	public HttpSolrClient getSearchClient(String urlString) {
		
		
		return new HttpSolrClient.Builder(urlString).build();
	}
	
	@Override
	public HttpSolrClient getSearchClientWithTable(String urlString, String tableName) {
	
		return new HttpSolrClient.Builder(urlString+"/"+tableName).build();
	}
 
}
