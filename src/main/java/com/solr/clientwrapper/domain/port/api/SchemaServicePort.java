package com.solr.clientwrapper.domain.port.api;


import com.solr.clientwrapper.domain.dto.schema.FieldDTO;
import com.solr.clientwrapper.domain.dto.schema.SchemaDTO;
import com.solr.clientwrapper.domain.dto.schema.SchemaResponseDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface SchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SchemaResponseDTO create(String tableName, SchemaDTO newSchemaDTO);
	SchemaResponseDTO update(String tableName, SchemaDTO schemaDTO);
	SchemaResponseDTO delete(String tableName);
	SchemaResponseDTO get(String tableName);
	
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
