package com.solr.clientwrapper.usecase.solrsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSearchRecordsServicePort;

@Service
@Transactional
public class SolrSearchOrdered {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchOrdered.class);
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	public SolrSearchOrdered(SolrSearchRecordsServicePort solrSearchRecordsServicePort) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
	}
	
	public SolrSearchResponseDTO orderedSearch(
			String tableName, 
			String queryField, 
			String queryFieldSearchTerm, 
			String sortTag, 
			String sortOrder
			) {
		logger.debug("Ordered search for the given table");
		return solrSearchRecordsServicePort.setUpSelectQueryOrderedSearch(
				tableName, 
				queryField, 
				queryFieldSearchTerm, 
				sortTag, 
				sortOrder);
	}
}
