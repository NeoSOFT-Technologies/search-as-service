package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.infrastructure.solrbean.SolrSearchResult;

public interface SolrSearchOperationServicePort {

	SolrSearchResult setUpSelectQuery(	String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, 
										String order);
	
	SolrSearchResult setUpSelectQueryWithPagination(	String collection, 
														String queryField, 
														String searchTerm, 
														String startRecord, 
														String pageSize, 
														String tag, 
														String order, 
														String startPage);
}
