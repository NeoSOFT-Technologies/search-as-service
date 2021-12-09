package com.solr.clientwrapper.domain.port.api;

import java.util.List;
import java.util.Map;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;

public interface SolrSchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SolrSchemaDTO create(String tableName, String name, SolrSchemaDTO newSolrSchemaDTO);
	SolrSchemaDTO update(String tableName, String name, SolrSchemaDTO solrSchemaDTO);
	SolrSchemaDTO update1(String tableName, String name, SolrSchemaDTO newSolrSchemaDTO);
	void delete(String tableName, String name);
	SolrSchemaDTO get(String tableName, String name);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	String validateSchema();
	String storeSchema();
	String getSolrClient(String tableName);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(SolrFieldDTO solrFieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SolrSchemaDTO solrSchemaDTO);

}
