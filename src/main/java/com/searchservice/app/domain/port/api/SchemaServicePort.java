package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.schema.FieldDTO;
import com.searchservice.app.domain.dto.schema.SchemaDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface SchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SchemaDTO create(String tableName, SchemaDTO newSchemaDTO);
	SchemaDTO update(String tableName, SchemaDTO schemaDTO);
	SchemaDTO delete(String tableName);
	SchemaDTO get(String tableName);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(SchemaDTO schemaDTO);
	boolean validateSchemaField(FieldDTO fieldDTO);
	boolean validateSchemaFieldBooleanAttributes(FieldDTO fieldDTO);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(FieldDTO fieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(FieldDTO fieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SchemaDTO schemaDTO);

}
