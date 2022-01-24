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
public class UpdateSolrSchema {

	private final Logger log = LoggerFactory.getLogger(UpdateSolrSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public UpdateSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaResponseDTO update(String tableName, SolrSchemaDTO solrSchemaDTO) {
		log.debug("update Schema");
		return solrSchemaServicePort.update(tableName, solrSchemaDTO);
	}

}
