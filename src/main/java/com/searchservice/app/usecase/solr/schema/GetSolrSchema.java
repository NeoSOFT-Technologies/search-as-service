package com.searchservice.app.usecase.solr.schema;


import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import com.searchservice.app.domain.port.api.SolrSchemaServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GetSolrSchema {

	private final Logger log = LoggerFactory.getLogger(GetSolrSchema.class);

	private final SolrSchemaServicePort solrSchemaServicePort;

	public GetSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaResponseDTO get(String tableName) {
		log.debug("get Schema");
		return solrSchemaServicePort.get(tableName);
	}

}
