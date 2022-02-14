package com.searchservice.app.domain.utils;

public class SchemaFieldType {

	public static final String Boolean = "boolean";

	public static String fromStandardDataTypeToSolrFieldType(String dataType) {

		switch (dataType) {
		case Boolean:
			return Boolean;
		case "Boolean":
			return Boolean;
		case "long":
			return "plong";
		case "Long":
			return "plong";
		case "date":
			return "pdate";
		case "Date":
			return "pdate";
		case "int":
			return "pint";
		case "Int":
			return "pint";
		case "double":
			return "pdouble";
		case "Double":
			return "pdouble";
		case "text":
			return "text_general";
		case "Text":
			return "text_general";
		case "float":
			return "pfloat";
		case "Float":
			return "pfloat";
		default:
			return "string";
		}
	}

	public static String fromSolrFieldTypeToStandardDataType(String fieldType) {
		switch (fieldType) {
		case Boolean:
			return Boolean;
		case "plong":
			return "long";
		case "pint":
			return "int";
		case "pfloat":
			return "float";
		case "pdouble":
			return "double";
		case "pdate" :
			return "Date";
		default:
			return "string";

		}
	}
}
