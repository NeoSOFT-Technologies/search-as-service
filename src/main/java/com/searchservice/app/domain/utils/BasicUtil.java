package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.searchservice.app.domain.dto.table.SchemaField;

public class BasicUtil {
    public static HashMap<String, SchemaField> convertSchemaFieldListToHashMap(
    		List<SchemaField> list)
    {
        HashMap<String, SchemaField> hashMap = new HashMap<>();
  
        for (SchemaField dto : list) {
  
            hashMap.put(dto.getName(), dto);
        }
  
        return hashMap;
    }
}
