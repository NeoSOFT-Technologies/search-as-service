package com.searchservice.app.domain.port.api;


import com.searchservice.app.domain.dto.SolrFieldDTO;
import com.searchservice.app.domain.dto.SolrSchemaDTO;
import com.searchservice.app.domain.dto.SolrSchemaResponseDTO;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import java.util.List;
import java.util.Map;

public interface SolrSchemaServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SolrSchemaResponseDTO create(String tableName, SolrSchemaDTO newSolrSchemaDTO);
	SolrSchemaResponseDTO update(String tableName, SolrSchemaDTO solrSchemaDTO);
	SolrSchemaResponseDTO delete(String tableName);
	SolrSchemaResponseDTO get(String tableName);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(SolrSchemaDTO solrSchemaDTO);
	boolean validateSchemaField(SolrFieldDTO solrFieldDTO);
	boolean validateSchemaFieldBooleanAttributes(SolrFieldDTO solrFieldDTO);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(SolrFieldDTO solrFieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SolrSchemaDTO solrSchemaDTO);

}
