package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;

public interface SolrSearchRecordsServicePort {
	SolrSearchResponseDTO setUpSelectQueryUnfiltered(	
										String collection);
	
	SolrSearchResponseDTO setUpSelectQueryBasicSearch(	
										String collection, 
										String queryField, 
										String searchTerm);
	
	SolrSearchResponseDTO setUpSelectQueryOrderedSearch(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String tag, 
										String order);
	
	SolrSearchResponseDTO setUpSelectQueryAdvancedSearch(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, 
										String order);
	
	SolrSearchResponseDTO setUpSelectQueryAdvancedSearchWithPagination(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, String order,
										String startPage);
}
