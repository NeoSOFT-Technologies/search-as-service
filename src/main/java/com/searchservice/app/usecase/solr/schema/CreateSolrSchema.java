package com.searchservice.app.usecase.solr.schema;


import com.searchservice.app.domain.dto.SolrSchemaDTO;
import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import com.searchservice.app.domain.port.api.SolrSchemaServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateSolrSchema {

	private final Logger log = LoggerFactory.getLogger(CreateSolrSchema.class);

	private final SolrSchemaServicePort solrSchemaServicePort;

	public CreateSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}
	
	public SolrSchemaResponseDTO create(String tableName, SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("create Schema");
		return solrSchemaServicePort.create(tableName, newSolrSchemaDTO);
	}
}
