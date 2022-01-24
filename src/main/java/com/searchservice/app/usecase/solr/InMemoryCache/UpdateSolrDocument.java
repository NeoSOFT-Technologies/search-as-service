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
public class UpdateSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(UpdateSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public UpdateSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	
	//@CachePut(value = "solrcache",key = "#tableName")
	public SolrDocumentResponseDTO update(String tableName, String name, SolrDocumentDTO solrDocumentDTO) {
		log.debug("update Schema");
		return inMemoryCacheServicePort.update(tableName, name, solrDocumentDTO);
	}

}
