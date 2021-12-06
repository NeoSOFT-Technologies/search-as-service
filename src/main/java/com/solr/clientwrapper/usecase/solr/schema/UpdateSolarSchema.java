package com.solr.clientwrapper.usecase.solr.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.port.api.SolrSchemaServicePort;

@Service
@Transactional
public class UpdateSolarSchema {

	private final Logger log = LoggerFactory.getLogger(UpdateSolarSchema.class);
	private final SolrSchemaServicePort solrSchemaServicePort;

	public UpdateSolarSchema(SolrSchemaServicePort solrSchemaServicePort) {
		this.solrSchemaServicePort = solrSchemaServicePort;
	}

	public SolrSchemaDTO update(String tableName,String name,SolrSchemaDTO solrSchemaDTO) {
		log.debug("update Schema");
		return solrSchemaServicePort.update(tableName, name,solrSchemaDTO);
	}

}
