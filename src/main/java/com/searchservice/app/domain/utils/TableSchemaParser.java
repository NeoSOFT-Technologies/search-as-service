package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
import com.searchservice.app.domain.dto.table.TableSchemaDTO;

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
	
	
	public static  List<Map<String, Object>> parseSchemaFieldDtosToListOfMaps(TableSchemaDTO tableSchemaDTO) {
		List<Map<String, Object>> schemaFieldsList = new ArrayList<>();
		SchemaFieldDTO[] schemaFields = tableSchemaDTO.getAttributes().toArray(new SchemaFieldDTO[0]);
		
		Map<String, Object> fieldDtoMap = new HashMap<>();
		for(SchemaFieldDTO fieldDto: schemaFields) {
			logger.debug("Validate SolrFieldDTO before parsing it");
			if(!validateSchemaField(fieldDto)) {
				fieldDtoMap = new HashMap<>();
				fieldDtoMap.put(VALIDATED, false);
				return schemaFieldsList;
			}
			fieldDtoMap.put("name", fieldDto.getName());
			fieldDtoMap.put("type", SchemaFieldType.fromObject(fieldDto.getType()));
			fieldDtoMap.put(STORED, fieldDto.isStorable());
			fieldDtoMap.put(MULTIVALUED, fieldDto.isMultiValue());
			fieldDtoMap.put(REQUIRED, fieldDto.isRequired());
			fieldDtoMap.put(DOCVALUES, fieldDto.isSortable());
			fieldDtoMap.put(INDEXED, fieldDto.isFilterable());
			schemaFieldsList.add(fieldDtoMap);
		}
		return schemaFieldsList;
	}
	
	
	public static boolean validateSchemaField(SchemaFieldDTO solrFieldDTO) {
		logger.debug("Validate schema field: {}", solrFieldDTO);
		boolean fieldValidated = true;
		String fieldName = solrFieldDTO.getName();
		String fieldType = solrFieldDTO.getType();
		
		if(fieldName.length() < 1) {
			fieldValidated = false;
			logger.debug("Invalid schema field name received: {}", fieldName);
		} else if(fieldType == null) {
			fieldValidated = false;
			logger.debug("Invalid/Empty schema field type received: {}", fieldType);
		} else if(!validateSchemaFieldBooleanAttributes(solrFieldDTO)) {
			fieldValidated = false;
			logger.debug("Invalid/Empty schema field boolean attributes received");
		}
		return fieldValidated;
	}
	
	
	public static boolean validateSchemaFieldBooleanAttributes(SchemaFieldDTO solrFieldDTO) {
		logger.debug("Validate schema field boolean attributes: {}", solrFieldDTO);
		
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
			logger.debug("Invalid entry for field attribute: \"{}\"", invalidAttribute);
		logger.debug("All Schema field boolean attributes are valid");
		return fieldAttributesValidated;
	}
	
	
	public static void setFieldsToDefaults(SchemaFieldDTO solrFieldDTO) {
		solrFieldDTO.setFilterable(false);
		solrFieldDTO.setMultiValue(false);
		solrFieldDTO.setDefault_("mydefault");
		solrFieldDTO.setRequired(false);
		solrFieldDTO.setSortable(false);
		solrFieldDTO.setStorable(true);
	}
	
	
	public static void setFieldsAsPerTheSchema(SchemaFieldDTO solrFieldDTO, Map<String, Object> schemaField) {
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
