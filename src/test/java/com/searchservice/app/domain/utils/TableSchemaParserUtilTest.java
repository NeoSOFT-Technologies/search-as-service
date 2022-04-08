package com.searchservice.app.domain.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;

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
	
	SchemaField schemaFieldDTO;
	
	@BeforeEach
	public void setUp() {
		 schemaFieldDTO = new SchemaField();
		 schemaFieldDTO.setSortable(true);
	}
	@Test
	void validateSchemaInvalidName() {
		schemaFieldDTO.setName("");
		assertFalse(TableSchemaParserUtil.validateSchemaField(schemaFieldDTO));
	}
	
	@Test
	void validateSchemaInvalidType() {
		schemaFieldDTO.setName("fname");
		schemaFieldDTO.setType(null);
		assertFalse(TableSchemaParserUtil.validateSchemaField(schemaFieldDTO));
	}
	
	@Test
	void validateSchemaInvalidRequired() {
		schemaFieldDTO.setName("fname");
		schemaFieldDTO.setType("string");
		TableSchemaParserUtil.validateSchemaField(schemaFieldDTO);
	}
	
}
