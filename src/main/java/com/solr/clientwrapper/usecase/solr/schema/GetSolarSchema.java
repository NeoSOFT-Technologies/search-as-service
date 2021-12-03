package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class GetSolarSchema {

	private final Logger log = LoggerFactory.getLogger(GetSolarSchema.class);

	private final SolrSchemaServicePort solrSchemaServicePort;

	public GetSolarSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaDTO get(String tableName,String name) {
		log.debug("get Schema");
		return solrSchemaServicePort.get(tableName, name);

	}

}
