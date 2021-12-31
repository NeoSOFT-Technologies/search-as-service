package com.solr.clientwrapper.usecase.solrsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSearchRecordsServicePort;

@Service
@Transactional
public class SolrSearchUnfiltered {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchUnfiltered.class);
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	public SolrSearchUnfiltered(SolrSearchRecordsServicePort solrSearchRecordsServicePort) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
	}
	
	public SolrSearchResponseDTO unfilteredSearch(
			String tableName
			) {
		logger.debug("Unfiltered search for the given table");
		return solrSearchRecordsServicePort.setUpSelectQueryUnfiltered(
				tableName);
	}
}
