package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableSchema;

public class TableSchemaParser {
	private static final Logger logger = LoggerFactory.getLogger(TableSchemaParser.class);
	private TableSchemaParser() {}

	
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String VALIDATED = "validated";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	
	
	public static  List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(TableSchema tableSchemaDTO) {
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();
		
		for(SchemaField fieldDto: tableSchemaDTO.getColumns()) {

			logger.info("Validate SearchFieldDTO before parsing it");

			Map<String, Object> fieldDtoMap = new HashMap<>();
			if(!validateSchemaField(fieldDto)) {
				
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsListOfMap;
			}
			if(isFieldUnchangeable(fieldDto.getName()))
				continue;
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SchemaFieldType.fromStandardDataTypeToSearchFieldType(fieldDto.getType(),fieldDto.isMultiValue()));
			fieldDtoMap.put(STORED, fieldDto.isStorable());
			fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
			fieldDtoMap.put(REQUIRED, fieldDto.isRequired());

			schemaFieldsListOfMap.add(fieldDtoMap);

			fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
			fieldDtoMap.put(INDEXED, fieldDto.isFilterable());
			schemaFieldsListOfMap.add(fieldDtoMap);
		}
		return schemaFieldsListOfMap;
	}
	
	

	public static boolean validateSchemaField(SchemaField searchFieldDTO) {
		logger.info("Validate schema field: {}", searchFieldDTO);

		
		boolean fieldValidated = true;
		String fieldName = searchFieldDTO.getName();
		String fieldType = searchFieldDTO.getType();
		
		// If DOCVALUES == TRUE(=> SORTABLE == TRUE), then MULTIVALUED = FALSE
		if(searchFieldDTO.isSortable())
			searchFieldDTO.setMultiValue(false);
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			logger.info("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null) {
			fieldValidated = false;
			logger.info("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(searchFieldDTO)) {
			fieldValidated = false;
			logger.info("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}
	
	

	public static boolean validateSchemaFieldBooleanAttributes(SchemaField searchFieldDTO) {
		logger.info("Validate schema field boolean attributes: {}", searchFieldDTO);

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
			logger.info("Invalid entry for field attribute: \"{}\"", invalidAttribute);
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
}
