package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.schema.FieldTypeDefinition;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SolrJAdapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Transactional
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableSchemaParser {
	
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String VALIDATED = "validated";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	
	private static final Logger logger = LoggerFactory.getLogger(TableSchemaParser.class);

	@Value("${base-search-url}")
	public String searchURL;
	
	@Autowired
	SolrJAdapter searchjAdapter;
//	SolrJAdapter searchjAdapter = new SolrJAdapter();
	
	@Autowired
	SearchAPIAdapter searchAPIAdapter;

	public TableSchemaParser(SolrJAdapter searchjAdapter, SearchAPIAdapter searchAPIAdapter) {
		this.searchjAdapter = searchjAdapter;
		this.searchAPIAdapter = searchAPIAdapter;
	}
	
	
	public static  List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(TableSchema tableSchemaDTO) {
		
		SolrJAdapter searchjAdapterTemp = new SolrJAdapter();
		
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();
		
		for(SchemaField fieldDto: tableSchemaDTO.getColumns()) {
			Map<String, Object> fieldDtoMap = new HashMap<>();
			if(!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsListOfMap;
			}
			if(isFieldUnchangeable(fieldDto.getName()))
				continue;
			
			///////////////////////////////// PARTIAL_SEARCH UPDATE
			// if partial search enabled	
			if (fieldDto.isPartialSearch()) {
				
				Map<String, Object> fieldTypeAttributes = new HashMap<>();
				
				// Add <partial-search> field-type if not present already				
				searchjAdapterTemp.createPartialSearchFieldTypeIfNotPresent(tableSchemaDTO, fieldTypeAttributes);

				// Add <partial-search> fieldType to the field
				fieldDtoMap.put("type", fieldTypeAttributes.get("name"));
				// Since "partial search" is enabled on this field, docValues has to be disabled
				fieldDto.setSortable(false);
				
			} else {
				fieldDtoMap.put("type", SchemaFieldType.fromStandardDataTypeToSearchFieldType(fieldDto.getType(),
 						fieldDto.isMultiValue()));
				fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
			}
			/////////////////////////////////

			fieldDtoMap.put("name", fieldDto.getName());
 			fieldDtoMap.put(STORED, fieldDto.isStorable());
			fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
			fieldDtoMap.put(REQUIRED, fieldDto.isRequired());
			fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
			fieldDtoMap.put(INDEXED, fieldDto.isFilterable());
			schemaFieldsListOfMap.add(fieldDtoMap);
		}
		return schemaFieldsListOfMap;
	}
	
	

	public static boolean validateSchemaField(SchemaField searchFieldDTO) {
		logger.debug("Validate schema field: {}", searchFieldDTO);
		
		boolean fieldValidated = true;
		String fieldName = searchFieldDTO.getName();
		String fieldType = searchFieldDTO.getType();
		
		// If DOCVALUES == TRUE(=> SORTABLE == TRUE), then MULTIVALUED = FALSE
		if(searchFieldDTO.isSortable())
			searchFieldDTO.setMultiValue(false);
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			logger.debug("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null) {
			fieldValidated = false;
			logger.debug("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(searchFieldDTO)) {
			fieldValidated = false;
			logger.debug("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}
	

	public static boolean validateSchemaFieldBooleanAttributes(SchemaField searchFieldDTO) {
		logger.debug("Validate schema field boolean attributes: {}", searchFieldDTO);

		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if(!searchFieldDTO.isRequired() && searchFieldDTO.isRequired()) {
			fieldAttributesValidated = false;
			invalidAttribute = REQUIRED;
		} else if(!searchFieldDTO.isFilterable() && searchFieldDTO.isFilterable()) {
			fieldAttributesValidated = false;
			invalidAttribute = INDEXED;
		} else if(!searchFieldDTO.isMultiValue() && searchFieldDTO.isMultiValue()) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if(!searchFieldDTO.isStorable() && searchFieldDTO.isStorable()) {
			fieldAttributesValidated = false;
			invalidAttribute = STORED;
		} else if(!searchFieldDTO.isSortable() && searchFieldDTO.isSortable()) {
			fieldAttributesValidated = false;
			invalidAttribute = DOCVALUES;
		}
		if(!fieldAttributesValidated)
			logger.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		logger.info("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}
	
	
	public static boolean isFieldUnchangeable(String fieldName) {
		Pattern pattern = Pattern.compile("^(_)+([a-zA-Z_$][a-zA-Z\\d_$]*)(_)+$");
        Matcher matcher = pattern.matcher(fieldName);

		return matcher.matches() || fieldName.equals("id");
	}
	
	
	public static void setFieldsToDefaults(SchemaField searchFieldDTO) {
		searchFieldDTO.setFilterable(false);
		searchFieldDTO.setMultiValue(false);
		searchFieldDTO.setRequired(false);
		searchFieldDTO.setSortable(false);
		searchFieldDTO.setStorable(true);
	}
	
	
	public static void setFieldsAsPerTheSchema(SchemaField searchFieldDTO, Map<String, Object> schemaField) {
		
		
		
		if(schemaField.containsKey(INDEXED))
			searchFieldDTO.setFilterable((boolean)schemaField.get(INDEXED));
		if(schemaField.containsKey(MULTIVALUED))
			searchFieldDTO.setMultiValue((boolean)schemaField.get(MULTIVALUED));
		if(schemaField.containsKey(REQUIRED))
			searchFieldDTO.setRequired((boolean)schemaField.get(REQUIRED));
		if(schemaField.containsKey(DOCVALUES))
			searchFieldDTO.setSortable((boolean)schemaField.get(DOCVALUES));
		if(schemaField.containsKey(STORED))
			searchFieldDTO.setStorable((boolean)schemaField.get(STORED));
		if(schemaField.get("type").equals(PARTIAL_SEARCH)) {
			searchFieldDTO.setPartialSearch(true);
		}

	}
	
	
	public void createPartialSearchFieldTypeIfNotPresent(TableSchema tableSchemaDTO, Map<String, Object> fieldTypeAttributes) {
			
		if (!searchjAdapter.isPartialSearchFieldTypePresent(tableSchemaDTO.getTableName())) {
			FieldTypeDefinition fieldTypeDef = new FieldTypeDefinition();
			fieldTypeAttributes = getFieldTypeAttributesForPartialSearch();
			fieldTypeDef.setAttributes(fieldTypeAttributes);
			SchemaRequest.AddFieldType addFieldTypeRequest = new SchemaRequest.AddFieldType(
					fieldTypeDef);

			HttpSolrClient searchClientActive = searchAPIAdapter.getSearchClientWithTable(searchURL,
					tableSchemaDTO.getTableName());
			searchjAdapter.addFieldTypeRequest(addFieldTypeRequest, searchClientActive);

		} else
			fieldTypeAttributes.put("name", PARTIAL_SEARCH);
	}
	
	
	// Partial Search Field Type
	public static Map<String, Object> getFieldTypeAttributesForPartialSearch() {
		final String FIELD_TYPE_CLASS = "class";
		final String FIELD_TYPE_NAME = "name";

		Map<String, Object> partialSearchFieldTypeAttrs = new HashMap<>();
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_CLASS, "solr.TextField");
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_NAME, "partial_search");
		partialSearchFieldTypeAttrs.put("positionIncrementGap", "100");

		Map<String, Object> analyzerObject = new HashMap<>();
		// Prepare charFilters
		Map<String, Object> charFilter = new HashMap<>();
		charFilter.put(FIELD_TYPE_CLASS, "solr.PatternReplaceCharFilterFactory");
		charFilter.put("replacement", "$1$1");
		charFilter.put("pattern", "([a-zA-Z])\\\\1+");
		// Prepare tokenizer
		Map<String, Object> tokenizerObject = new HashMap<>();
		tokenizerObject.put(FIELD_TYPE_CLASS, "solr.WhitespaceTokenizerFactory");
		// Prepare filters
		Map<String, Object> filterObject1 = new HashMap<>();
		Map<String, Object> filterObject2 = new HashMap<>();
		filterObject1.put(FIELD_TYPE_CLASS, "solr.WordDelimiterFilterFactory");
		filterObject1.put("preserveOriginal", "0");
		filterObject2.put(FIELD_TYPE_CLASS, "solr.NGramTokenizerFactory");
		filterObject2.put("maxGramSize", "25");
		filterObject2.put("minGramSize", "3");
		Map<String, Object> filtersObject = new HashMap<>();
		filtersObject.put("filters", Arrays.asList(filterObject1, filterObject2));
		// Add charFilters, tokenizer & filters to analyzer
		analyzerObject.put("charFilters", Arrays.asList(charFilter));
		analyzerObject.put("tokenizer", tokenizerObject);
		analyzerObject.put("filters", Arrays.asList(filterObject1, filterObject2));

		Object analyzerFinalObject = analyzerObject;
		partialSearchFieldTypeAttrs.put("analyzer", analyzerFinalObject);

		return partialSearchFieldTypeAttrs;
	}
}
