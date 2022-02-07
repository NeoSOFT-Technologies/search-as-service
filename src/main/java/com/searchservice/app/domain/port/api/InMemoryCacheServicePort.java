package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
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
	boolean validateSchemaField(SchemaFieldDTO fieldDTO);
	boolean validateSchemaFieldBooleanAttributes(SchemaFieldDTO fieldDTO);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(SchemaFieldDTO fieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(SchemaFieldDTO fieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(DocumentDTO documentDTO);

}
