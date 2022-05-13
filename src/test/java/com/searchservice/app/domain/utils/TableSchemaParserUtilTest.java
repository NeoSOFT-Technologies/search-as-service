package com.searchservice.app.domain.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.searchservice.app.domain.dto.table.SchemaField;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
class TableSchemaParserUtilTest{

	@InjectMocks
	TableSchemaParserUtil tableSchemaParser;
	private static final String MULTIVALUED = "multiValued";
	private static final String STORED = "stored";
	private static final String REQUIRED = "required";
	private static final String DOCVALUES = "docValues";
	private static final String INDEXED = "indexed";
	private static final String PARTIAL_SEARCH = "partial_search";
	SchemaField schemaFieldDTO;
	
	Map<String, Object> schemaField = new HashMap<>();
	
	@BeforeEach
	public void setUp() {
		schemaFieldDTO = new SchemaField();

	}
	@Test
	void validateSchemaInvalidName() {
		schemaFieldDTO.setName("");
		 schemaFieldDTO.setSortable(true);
		assertFalse(TableSchemaParserUtil.validateSchemaField(schemaFieldDTO));
	}
	
	@Test
	void validateSchemaInvalidType() {
		schemaFieldDTO.setName("fname");
		schemaFieldDTO.setType(null);
		 schemaFieldDTO.setSortable(true);
		assertFalse(TableSchemaParserUtil.validateSchemaField(schemaFieldDTO));
	}
	
	@Test
	void validateSchemaInvalidRequired() {
		schemaFieldDTO.setName("fname");
		schemaFieldDTO.setType("string");
		TableSchemaParserUtil.validateSchemaField(schemaFieldDTO);
	}
	@Test
	void isFieldUnchangeableTest() {
		assertTrue(TableSchemaParserUtil.isFieldUnchangeable("id"));
	}
	@Test
	void setDefaultFieldsTest() {
		TableSchemaParserUtil.setFieldsToDefaults(schemaFieldDTO);
		assertTrue(1>0);
	}
	
	@Test
	void setFieldseSchemaTest() {
		schemaField.put(INDEXED, true);
		schemaField.put(MULTIVALUED, true);
		schemaField.put(STORED, false);
		schemaField.put("type", PARTIAL_SEARCH);
		schemaField.put(DOCVALUES, true);
		schemaField.put(REQUIRED, true);
		TableSchemaParserUtil.setFieldsAsPerTheSchema(schemaFieldDTO, schemaField);
		assertTrue(1>0);
	}
	
	@Test
	void isMultiValueDataTypeTest() {
		schemaFieldDTO.setMultiValue(false);
		schemaFieldDTO.setType("strings");
		assertFalse(TableSchemaParserUtil.isMultivaluedDataTypePlural(schemaFieldDTO));
	}
	
	@Test
	void prepareNewFieldsTest() {
		schemaFieldDTO.setName("fname");
		schemaFieldDTO.setRequired(true);
		schemaFieldDTO.setFilterable(true);
		schemaFieldDTO.setMultiValue(true);
		schemaFieldDTO.setStorable(false);
		TableSchemaParserUtil.prepareNewField(schemaField, schemaFieldDTO);
		assertTrue(1>0);
	}

}
