package com.solr.clientwrapper.usecase.solr.InMemoryCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrInMemoryCacheServicePort;
@Service
@Transactional
public class GetSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(GetSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public GetSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	//@Cacheable(value = "solrcache",key = "#tableName")
	public SolrDocumentResponseDTO get(String tableName,String name) {
		log.debug("Get Schema");
		return inMemoryCacheServicePort.get(tableName, name);
	}

}
