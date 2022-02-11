package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.searchservice.app.domain.dto.table.SchemaFieldDTO;

public class BasicUtil {
    public static HashMap<String, SchemaFieldDTO> convertSchemaFieldListToHashMap(
    		List<SchemaFieldDTO> list)
    {
        HashMap<String, SchemaFieldDTO> hashMap = new HashMap<>();
  
        for (SchemaFieldDTO dto : list) {
  
            hashMap.put(dto.getName(), dto);
        }
  
        return hashMap;
    }
}
