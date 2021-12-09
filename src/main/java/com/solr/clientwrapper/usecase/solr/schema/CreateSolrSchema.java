package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class CreateSolrSchema {

	private final Logger log = LoggerFactory.getLogger(CreateSolrSchema.class);

	private final SolrSchemaServicePort solrSchemaServicePort;

	public CreateSolrSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}
	
	public SolrSchemaDTO create(String tableName,String name, SolrSchemaDTO newSolrSchemaDTO) {
		log.debug("create Schema");
		return solrSchemaServicePort.create(tableName, name, newSolrSchemaDTO);
	}
}
