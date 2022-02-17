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
	private static final String DEFAULT = "default";
	
	
	public static  List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(TableSchema tableSchemaDTO) {
		List<Map<String, Object>> schemaFieldsListOfMap = new ArrayList<>();
		
		for(SchemaField fieldDto: tableSchemaDTO.getColumns()) {
			logger.info("Validate SolrFieldDTO before parsing it");
			Map<String, Object> fieldDtoMap = new HashMap<>();
			if(!validateSchemaField(fieldDto)) {
				logger.info("{} field couldn't be validated", fieldDto);
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsListOfMap;
			}
			if(isFieldUnchangeable(fieldDto.getName()))
				continue;
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SchemaFieldType.fromStandardDataTypeToSolrFieldType(fieldDto.getType(),fieldDto.isMultiValue()));
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
	
	
	public static boolean validateSchemaField(SchemaField solrFieldDTO) {
		logger.info("Validate schema field: {}", solrFieldDTO);
		
		solrFieldDTO.setDefault_(DEFAULT);
		
		boolean fieldValidated = true;
		String fieldName = solrFieldDTO.getName();
		String fieldType = solrFieldDTO.getType();
		
		// If DOCVALUES == TRUE(=> SORTABLE == TRUE), then MULTIVALUED = FALSE
		if(solrFieldDTO.isSortable())
			solrFieldDTO.setMultiValue(false);
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			logger.info("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null) {
			fieldValidated = false;
			logger.info("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(solrFieldDTO)) {
			fieldValidated = false;
			logger.info("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}
	
	
	public static boolean validateSchemaFieldBooleanAttributes(SchemaField solrFieldDTO) {
		logger.info("Validate schema field boolean attributes: {}", solrFieldDTO);
		
		boolean fieldAttributesValidated = true;
		String invalidAttribute = "";
		if(!solrFieldDTO.isRequired() && solrFieldDTO.isRequired()) {
			fieldAttributesValidated = false;
			invalidAttribute = REQUIRED;
		} else if(!solrFieldDTO.isFilterable() && solrFieldDTO.isFilterable()) {
			fieldAttributesValidated = false;
			invalidAttribute = INDEXED;
		} else if(!solrFieldDTO.isMultiValue() && solrFieldDTO.isMultiValue()) {
			fieldAttributesValidated = false;
			invalidAttribute = "multValued";
		} else if(!solrFieldDTO.isStorable() && solrFieldDTO.isStorable()) {
			fieldAttributesValidated = false;
			invalidAttribute = STORED;
		} else if(!solrFieldDTO.isSortable() && solrFieldDTO.isSortable()) {
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
	
	
	public static void setFieldsToDefaults(SchemaField solrFieldDTO) {
		solrFieldDTO.setFilterable(false);
		solrFieldDTO.setMultiValue(false);
		solrFieldDTO.setDefault_("mydefault");
		solrFieldDTO.setRequired(false);
		solrFieldDTO.setSortable(false);
		solrFieldDTO.setStorable(true);
	}
	
	
	public static void setFieldsAsPerTheSchema(SchemaField solrFieldDTO, Map<String, Object> schemaField) {
		if(schemaField.containsKey(INDEXED))
			solrFieldDTO.setFilterable((boolean)schemaField.get(INDEXED));
		if(schemaField.containsKey(MULTIVALUED))
			solrFieldDTO.setMultiValue((boolean)schemaField.get(MULTIVALUED));
		if(schemaField.containsKey(DEFAULT))
			solrFieldDTO.setDefault_((String)schemaField.get(DEFAULT));
		if(schemaField.containsKey(REQUIRED))
			solrFieldDTO.setRequired((boolean)schemaField.get(REQUIRED));
		if(schemaField.containsKey(DOCVALUES))
			solrFieldDTO.setSortable((boolean)schemaField.get(DOCVALUES));
		if(schemaField.containsKey(STORED))
			solrFieldDTO.setStorable((boolean)schemaField.get(STORED));
	}
}
