package com.solr.clientwrapper.usecase.solrsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSearchRecordsServicePort;

@Service
@Transactional
public class SolrSearchBasic {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchBasic.class);
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	public SolrSearchBasic(SolrSearchRecordsServicePort solrSearchRecordsServicePort) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
	}
	
	public SolrSearchResponseDTO basicSearch(
			String tableName, 
			String queryField, 
			String queryFieldSearchTerm
			) {
		logger.debug("Basic search for the given table");
		return solrSearchRecordsServicePort.setUpSelectQueryBasicSearch(
				tableName, 
				queryField, 
				queryFieldSearchTerm);
	}
}
