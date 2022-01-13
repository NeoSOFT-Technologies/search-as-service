package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class DeleteSolrSchema {

	private final Logger log = LoggerFactory.getLogger(DeleteSolrSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public DeleteSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaResponseDTO delete(String tableName, String name) {
		log.debug("Calling delete schema field Port");
		return solrSchemaServicePort.delete(tableName, name);
	}

}
