package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;

public interface SolrSchemaServicePort {

	SolrSchemaDTO create(String tableName,String name,SolrFieldDTO[] attributes);
	SolrSchemaDTO update(String tableName,String name);
	SolrSchemaDTO delete(String tableName,String name);
	SolrSchemaDTO get(String tableName,String name);
}
