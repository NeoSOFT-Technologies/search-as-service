package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;

public interface SolrSchemaServicePort {

	SolrSchemaResponseDTO create(String tableName,String name,SolrFieldDTO[] attributes);
	SolrSchemaResponseDTO update(String tableName,String name, SolrSchemaDTO solrSchemaDTO);
	SolrSchemaResponseDTO delete(String tableName,String name);
	SolrSchemaResponseDTO get(String tableName,String name);
}
