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
public class CreateSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(CreateSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;
	
	public CreateSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}

	public SolrDocumentResponseDTO create(String tableName,String name, SolrDocumentDTO newSolrDocumentDTO) {
		log.debug("create Schema");
		return inMemoryCacheServicePort.create(tableName, name, newSolrDocumentDTO);
	}

}
