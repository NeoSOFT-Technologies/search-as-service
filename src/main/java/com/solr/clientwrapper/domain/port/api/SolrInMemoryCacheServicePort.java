package com.solr.clientwrapper.domain.port.api;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;

import com.solr.clientwrapper.domain.dto.solr.SolrFieldDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentDTO;
import com.solr.clientwrapper.domain.dto.solr.SolrDocumentResponseDTO;

public interface SolrInMemoryCacheServicePort {

	/* 
	 * CRUD operations on solr schema
	 */
	SolrDocumentResponseDTO create(String tableName, String name, SolrDocumentDTO newSolrDocumentDTO);
	SolrDocumentResponseDTO update(String tableName, String name, SolrDocumentDTO solrDocumentDTO);
	SolrDocumentResponseDTO delete(String tableName, String name);
	SolrDocumentResponseDTO get(String tableName, String name);
	
	/*
	 * Validation of solr schema and External API callings 
	 */
	List<FieldTypeDefinition> getSchemaFieldTypes(SolrDocumentDTO solrDocumentDTO);
	boolean validateSchemaField(SolrFieldDTO solrFieldDTO);
	boolean validateSchemaFieldBooleanAttributes(SolrFieldDTO solrFieldDTO);
	String getSolrClient(String tableName);

	/* 
	 * Logical operations in service methods 
	 */
	void setFieldsAsPerTheSchema(SolrFieldDTO solrFieldDTO, Map<String, Object> schemaField);
	void setFieldsToDefaults(SolrFieldDTO solrFieldDTO);
	List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(SolrDocumentDTO solrDocumentDTO);

}
