package com.searchservice.app.usecase.solr.InMemoryCache;


import com.searchservice.app.domain.dto.SolrDocumentResponseDTO;
import com.searchservice.app.domain.port.api.SolrInMemoryCacheServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetSolrDocument {
	
	private final Logger log = LoggerFactory.getLogger(GetSolrDocument.class);

	private final SolrInMemoryCacheServicePort inMemoryCacheServicePort;

	public GetSolrDocument(SolrInMemoryCacheServicePort inMemoryCacheServicePort) {
		this.inMemoryCacheServicePort = inMemoryCacheServicePort;
	}
	//@Cacheable(value = "solrcache",key = "#tableName")
	public SolrDocumentResponseDTO get(String tableName, String name) {
		log.debug("Get Schema");
		return inMemoryCacheServicePort.get(tableName, name);
	}

}
