package com.solr.clientwrapper.domain.port.api;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrSchemaResponseDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface SolrSchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SolrSchemaResponseDTO create(String tableName, String name, SolrSchemaDTO newSolrSchemaDTO);
	SolrSchemaResponseDTO update(String tableName, SolrSchemaDTO solrSchemaDTO);
	SolrSchemaResponseDTO delete(String tableName);
	SolrSchemaResponseDTO get(String tableName);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(SolrSchemaDTO solrSchemaDTO);
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
