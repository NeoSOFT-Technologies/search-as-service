package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.document.DocumentDTO;
import com.searchservice.app.domain.dto.document.DocumentResponseDTO;
import com.searchservice.app.domain.dto.schema.FieldDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface InMemoryCacheServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	DocumentResponseDTO create(String tableName, String name, DocumentDTO newDocumentDTO);
	DocumentResponseDTO update(String tableName, String name, DocumentDTO documentDTO);
	DocumentResponseDTO delete(String tableName, String name);
	DocumentResponseDTO get(String tableName, String name);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(DocumentDTO documentDTO);
	boolean validateSchemaField(FieldDTO fieldDTO);
	boolean validateSchemaFieldBooleanAttributes(FieldDTO fieldDTO);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(FieldDTO fieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(FieldDTO fieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(DocumentDTO documentDTO);

}
