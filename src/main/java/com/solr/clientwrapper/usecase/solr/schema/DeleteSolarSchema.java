package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class DeleteSolarSchema {

	private final Logger log = LoggerFactory.getLogger(DeleteSolarSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public DeleteSolarSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaDTO delete(String name) {
		log.debug("delete");
		return solrSchemaServicePort.delete(name);
	}

}
