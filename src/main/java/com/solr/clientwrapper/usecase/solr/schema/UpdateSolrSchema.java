package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class UpdateSolrSchema {

	private final Logger log = LoggerFactory.getLogger(UpdateSolrSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public UpdateSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaResponseDTO update(String tableName, String name, SolrSchemaDTO solrSchemaDTO) {
		log.debug("update Schema");
		return solrSchemaServicePort.update(tableName, name, solrSchemaDTO);
	}

}