package com.searchservice.app.domain.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.searchservice.app.domain.dto.table.SchemaField;

public class BasicUtil {
	private BasicUtil() {}
	
    public static Map<String, SchemaField> convertSchemaFieldListToHashMap(
    		List<SchemaField> list)
    {
        Map<String, SchemaField> hashMap = new HashMap<>();
  
        for (SchemaField dto : list) {
  
            hashMap.put(dto.getName(), dto);
        }
  
        return hashMap;
    }
}
