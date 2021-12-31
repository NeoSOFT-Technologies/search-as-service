package com.solr.clientwrapper.usecase.solrsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solrsearch.SolrSearchResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSearchRecordsServicePort;

@Service
@Transactional
public class SolrSearchPaginated {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchPaginated.class);
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	public SolrSearchPaginated(SolrSearchRecordsServicePort solrSearchRecordsServicePort) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
	}
	
	public SolrSearchResponseDTO paginatedSearch(
			String tableName, 
			String queryField, 
			String queryFieldSearchTerm, 
			String startRecord, 
			String pageSize, 
			String sortTag, 
			String sortOrder, 
			String startPage
			) {
		logger.debug("Paginated search for the given table");
		return solrSearchRecordsServicePort.setUpSelectQueryAdvancedSearchWithPagination(
				tableName, 
				queryField, 
				queryFieldSearchTerm, 
				startRecord, 
				pageSize, 
				sortTag, 
				sortOrder, 
				startPage);
	}
}
