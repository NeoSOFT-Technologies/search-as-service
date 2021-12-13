package com.solr.clientwrapper.domain.port.api;

import java.util.List;
import java.util.Map;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;

public interface SolrSchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SolrSchemaResponseDTO create(String tableName, String name, SolrSchemaDTO newSolrSchemaDTO);
	SolrSchemaResponseDTO update(String tableName, String name, SolrSchemaDTO solrSchemaDTO);
	SolrSchemaResponseDTO delete(String tableName, String name);
	SolrSchemaResponseDTO get(String tableName, String name);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	boolean validateSchema(SolrSchemaDTO solrSchemaDTO);
	boolean validateSchemaField(SolrFieldDTO solrFieldDTO);
	boolean validateSchemaFieldBooleanAttributes(SolrFieldDTO solrFieldDTO);
	String getSolrClient(String tableName);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(SolrFieldDTO solrFieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SolrSchemaDTO solrSchemaDTO);

}
