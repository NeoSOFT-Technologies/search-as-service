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
public class UpdateSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(UpdateSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public UpdateSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	
	public SolrDocumentResponseDTO update(String tableName, String name, SolrDocumentDTO solrDocumentDTO) {
		log.debug("update Schema");
		return inMemoryCacheServicePort.update(tableName, name, solrDocumentDTO);
	}

}
