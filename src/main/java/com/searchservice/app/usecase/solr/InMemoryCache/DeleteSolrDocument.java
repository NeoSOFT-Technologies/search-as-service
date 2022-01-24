package com.searchservice.app.usecase.solr.InMemoryCache;


import com.searchservice.app.domain.dto.SolrDocumentResponseDTO;
import com.searchservice.app.domain.port.api.SolrInMemoryCacheServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(DeleteSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public DeleteSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	
	
	//@CacheEvict(value="solrcache", key = "#tableName")
	public SolrDocumentResponseDTO delete(String tableName, String name) {
		log.debug("delete Schema");
		return inMemoryCacheServicePort.delete(tableName, name);
	}

}
