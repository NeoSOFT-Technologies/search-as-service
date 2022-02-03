package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.schema.FieldDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface InMemoryCacheServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	DocumentDTO create(String tableName, String name, DocumentDTO newDocumentDTO);
	DocumentDTO update(String tableName, String name, DocumentDTO documentDTO);
	DocumentDTO delete(String tableName, String name);
	DocumentDTO get(String tableName, String name);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(DocumentDTO documentDTO);
	boolean validateSchemaField(FieldDTO fieldDTO);
	boolean validateSchemaFieldBooleanAttributes(FieldDTO fieldDTO);
	String getSolrClient(String tableName);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(FieldDTO fieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(FieldDTO fieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(DocumentDTO documentDTO);

}
