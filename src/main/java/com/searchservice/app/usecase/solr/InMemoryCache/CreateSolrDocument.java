package com.searchservice.app.usecase.solr.InMemoryCache;


import com.searchservice.app.domain.dto.SolrDocumentDTO;
import com.searchservice.app.domain.dto.SolrDocumentResponseDTO;
import com.searchservice.app.domain.port.api.SolrInMemoryCacheServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class CreateSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(CreateSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;
	
	public CreateSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}

	public SolrDocumentResponseDTO create(String tableName, String name, SolrDocumentDTO newSolrDocumentDTO) {
		log.debug("create Schema");
		return inMemoryCacheServicePort.create(tableName, name, newSolrDocumentDTO);
	}

}
