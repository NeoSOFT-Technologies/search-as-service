package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

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
