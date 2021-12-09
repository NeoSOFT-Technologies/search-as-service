package com.solr.clientwrapper.infrastructure.Enum;

import org.apache.commons.lang3.StringUtils;

public enum SolrFieldType {
	_nest_path_,
	 ancestor_path,
	 binary,
	 boolean_,
	 booleans,
	 ignored,
	 location,
	 lowercase,
	 string,
	 pdate,
	 text_general,
	 text_en,
	 pint,
	 plong,
	 pdouble,
	 pfloat,
	 currency;
	
    public static Boolean doesExist(String name) {
        if(StringUtils.isBlank(name)) {
            return false;
        }

        for(SolrFieldType item : SolrFieldType.values()) {
            if(name.equals(fromEnumToString(item))) {
                return true;
            }
        }
        return false;
    }
	
	public static SolrFieldType fromObject(String fieldType) {
		if(fieldType.equals("string")) {
			return string;
		}
		else if(fieldType.equals("_nest_path_"))
			return _nest_path_;
		else if(fieldType.equals("boolean"))
			return boolean_;
		else if(fieldType.equals("booleans"))
			return booleans;
		else if(fieldType.equals("plong"))
			return plong;
		else if(fieldType.equals("pdate"))
			return pdate;
		else if(fieldType.equals("pint"))
			return pint;
		else if(fieldType.equals("text_general"))
			return text_general;
		else if(fieldType.equals("text_en"))
			return text_en;
		else if(fieldType.equals("pfloat"))
			return pfloat;
		else if(fieldType.equals("pdouble"))
			return pdouble;
		else if(fieldType.equals("currency"))
			return currency;
		else
			return null;
	}
	public static String fromEnumToString(SolrFieldType fieldType) {
		if(fieldType.equals(string)) {
			return "string";
		}
		else if(fieldType.equals(_nest_path_))
			return "_nest_path_";
		else if(fieldType.equals(boolean_))
			return "boolean";
		else if(fieldType.equals(booleans))
			return "booleans";
		else if(fieldType.equals(plong))
			return "plong";
		else if(fieldType.equals(pdate))
			return "pdate";
		else if(fieldType.equals(pint))
			return "pint";
		else if(fieldType.equals(text_general))
			return "text_general";
		else if(fieldType.equals(text_en))
			return "text_en";
		else if(fieldType.equals(pfloat))
			return "pfloat";
		else if(fieldType.equals(pdouble))
			return "pdouble";
		else if(fieldType.equals(currency))
			return "currency";
		else
			return null;
	}
 
}
