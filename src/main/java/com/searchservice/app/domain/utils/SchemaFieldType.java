package com.searchservice.app.domain.utils;

public class SchemaFieldType {

	private static final String BOOLEAN = "boolean";
	private static final String BOOLEAN_MULTIVALUE = "booleans";
    public static String fromStandardDataTypeToSearchFieldType(String dataType, boolean isMultivalue) {
    
    	StringBuilder newDataType = new StringBuilder();
    	
    	if(dataType.endsWith("s")) {
    		for(int i=0;i<dataType.length()-1;i++) newDataType.append(dataType.charAt(i));
    	}
    	else newDataType.append(dataType);
    	
        switch (newDataType.toString().toLowerCase()) {

            case BOOLEAN:
                if (isMultivalue)
                    return BOOLEAN_MULTIVALUE;
                else
                    return BOOLEAN;
            case "long":
                if (isMultivalue)
                    return "plongs";
                else
                    return "plong";

            case "date":
            	 if (isMultivalue)
                return "pdates";	
            	 else
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

    public static String fromSearchFieldTypeToStandardDataType(String fieldType, Object isMultivalue) {
        switch (fieldType) {
            case BOOLEAN:
            	return BOOLEAN;
            case BOOLEAN_MULTIVALUE:
            	return BOOLEAN_MULTIVALUE;
            case "plong":
            	return "long";
            case "plongs":
            	return "longs";
            case "pint":
            	return "int";
            case "pints":
            	return "ints";
            case "pfloat":
            	return "float";
            case "pfloats":
            	return "floats";
            case "pdouble":
            	return "double";
            case "pdoubles":
            	return "doubles";
            case "pdate":
            	return "Date";
            case "pdates":
            	return "Dates";
            default:
                if (isMultivalue != null && (boolean)isMultivalue)
                    return "strings";
                else
                    return "string";
        }
    }
}
