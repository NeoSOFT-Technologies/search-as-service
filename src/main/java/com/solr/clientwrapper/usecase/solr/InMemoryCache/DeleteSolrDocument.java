package com.solr.clientwrapper.usecase.solr.InMemoryCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrDocumentDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrInMemoryCacheServicePort;
@Service
@Transactional
public class DeleteSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(DeleteSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public DeleteSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	
	public SolrDocumentResponseDTO delete(String tableName, String name) {
		log.debug("create Schema");
		return inMemoryCacheServicePort.delete(tableName, name);
	}

}
