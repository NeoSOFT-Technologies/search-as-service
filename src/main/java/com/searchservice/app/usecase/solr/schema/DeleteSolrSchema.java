package com.searchservice.app.usecase.solr.schema;


import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import com.searchservice.app.domain.port.api.SolrSchemaServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteSolrSchema {

	private final Logger log = LoggerFactory.getLogger(DeleteSolrSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public DeleteSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaResponseDTO delete(String tableName) {
		log.debug("Calling delete schema field Port");
		return solrSchemaServicePort.delete(tableName);
	}

}
