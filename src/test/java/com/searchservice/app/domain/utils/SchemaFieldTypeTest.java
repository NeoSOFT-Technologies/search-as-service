package com.searchservice.app.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;



@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)

@SpringBootTest
class SchemaFieldTypeTest extends SchemaFieldType {

	
	@InjectMocks
	SchemaFieldType schemaFieldType;
	
	
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypeBoolean() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("boolean", true);
	assertEquals("boolean",a);
		
	}
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypeBooleans() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("booleans", true);
	assertEquals("booleans",a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypeLong() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("plong", true);
	assertEquals("long", a);
		
	}
	
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypeplongs() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("plongs", true);
	assertEquals("longs", a);
		
	}
	
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypepint() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pint", true);
	assertEquals("int", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypepints() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pints", true);
	assertEquals("ints", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypepfloat() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pfloat", true);
	assertEquals("float", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypepfloats() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pfloats", true);
	assertEquals("floats", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypepdouble() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pdouble", true);
	assertEquals("double", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypepdoubles() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pdoubles", true);
	assertEquals("doubles", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypedate() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pdate", true);
	assertEquals("Date", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypedates() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("pdates", true);
	assertEquals("Dates", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypestrings() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("strings", true);
	assertEquals("strings", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypestring() {
	String a=	SchemaFieldType.fromSearchFieldTypeToStandardDataType("strings", false);
	assertEquals("string", a);
		
	}
	
	
	//fromStandardDataTypeToSearchFieldType
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypeboolean() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("boolean", true);
	assertEquals("booleans", a);
		
	}
	
	@Test
	void testFromStandardDataTypeToSearchFieldTypebooleans() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("boolean", false);
	assertEquals("boolean", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypelong() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("long", true);
	assertEquals("plongs", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypelongs() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("long", false);
	assertEquals("plong", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypedate1() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("date", true);
	assertEquals("pdates", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypeint1() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("int", true);
	assertEquals("pints", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypeints1() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("int", false);
	assertEquals("pint", a);
		
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypedouble() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("double", true);
	assertEquals("pdoubles", a);
	}	
	@Test
	void testFromStandardDataTypeToSearchFieldTypedoubles() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("double", false);
	assertEquals("pdouble", a);
	}	
	@Test
	void testFromStandardDataTypeToSearchFieldTypetext() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("text", true);
	assertEquals("text_general", a);
	}	
	@Test
	void testFromStandardDataTypeToSearchFieldTypefloat() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("float", true);
	assertEquals("pfloats", a);
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypefloats() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("float", false);
	assertEquals("pfloat", a);
	}	
	@Test
	void testFromStandardDataTypeToSearchFieldTypestring1() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("string", true);
	assertEquals("strings", a);
	}
	@Test
	void testFromStandardDataTypeToSearchFieldTypestrings1() {
	String a=	SchemaFieldType.fromStandardDataTypeToSearchFieldType("string", false);
	assertEquals("string", a);
	}
}
