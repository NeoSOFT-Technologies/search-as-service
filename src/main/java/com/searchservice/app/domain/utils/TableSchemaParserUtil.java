package com.searchservice.app.domain.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.infrastructure.adaptor.SearchAPIAdapter;

import lombok.Data;

@Component
@Data
public class TableSchemaParserUtil {

	private static final String NAME = "name";
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	public static Map<String, Object> partialSearchFieldTypeAttrs = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(TableSchemaParserUtil.class);
	
	@Value("${base-search-url}")
	public String searchURL;
	
	@Autowired
	private SearchAPIAdapter searchAPIAdapter;
	
	public SearchAPIAdapter showAutowiredObj2() {
		return searchAPIAdapter;
	}
	
	static
	{
		final String FIELD_TYPE_CLASS = "class";
		final String FIELD_TYPE_NAME = "name";
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_CLASS, "solr.TextField");
		partialSearchFieldTypeAttrs.put(FIELD_TYPE_NAME, PARTIAL_SEARCH);
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
		
	}
	
	public static boolean validateSchemaField(SchemaField searchFieldDTO) {
		logger.debug("Validate schema field: {}", searchFieldDTO);
		
		boolean fieldValidated = true;
		String fieldName = searchFieldDTO.getName();
		String fieldType = searchFieldDTO.getType();
		
		// If MULTIVALUED = TRUE, then DOCVALUES(SORTABLE) has to be FALSE
		if(searchFieldDTO.isMultiValue())
			searchFieldDTO.setSortable(false);
		
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


	public static Map<String, Object> prepareNewField(Map<String, Object> newField, SchemaField fieldDto) {
		newField.put(NAME, fieldDto.getName());
		newField.put(REQUIRED, fieldDto.isRequired());
		newField.put(STORED, fieldDto.isStorable());
		newField.put(MULTIVALUED, fieldDto.isMultiValue());
		newField.put(INDEXED, fieldDto.isFilterable());
		return newField;
	}
	
	

}