package com.searchservice.app.domain.utils;

public class SchemaFieldType {

    public static String fromStandardDataTypeToSolrFieldType(String dataType, boolean isMultivalue) {

        switch (dataType.toLowerCase()) {

            case "boolean":
                if (isMultivalue)
                    return "booleans";
                else
                    return "boolean";
            case "long":
                if (isMultivalue)
                    return "plongs";
                else
                    return "plong";

            case "date":
                return "pdate";

            case "int":
                if (isMultivalue)
                    return "pints";
                else
                    return "pint";

            case "double":
                if (isMultivalue)
                    return "pdoubles";
                else
                    return "pdouble";
            case "text":
                return "text_general";

            case "float":
                if (isMultivalue)
                    return "pfloats";
                else
                    return "pfloat";
            default:
                if (isMultivalue)
                    return "strings";
                else
                    return "string";
        }
    }

    public static String fromSolrFieldTypeToStandardDataType(String fieldType) {
        switch (fieldType) {
            case "boolean":
                return "boolean";
            case "plong":
                return "long";
            case "pint":
                return "int";
            case "pfloat":
                return "float";
            case "pdouble":
                return "double";
            case "pdate":
                return "Date";
            default:
                return "string";

        }
    }
}
