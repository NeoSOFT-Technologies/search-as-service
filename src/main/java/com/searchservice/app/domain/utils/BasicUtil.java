package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.dto.table.SchemaField;

public class BasicUtil {
	
	private static final Logger log = LoggerFactory.getLogger(BasicUtil.class);
	
	private BasicUtil() {}
	
    public static Map<String, SchemaField> convertSchemaFieldListToHashMap(
    		List<SchemaField> list) {
        Map<String, SchemaField> hashMap = new HashMap<>();
        for (SchemaField dto : list) {
            hashMap.put(dto.getName(), dto);
        }
        return hashMap;
    }
    
    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> parseJsonArrayToListOfMaps(JSONArray jsonArray) {
    	List<Map<String, Object>> listOfMaps = new ArrayList<>(); 
    	
    	Iterator<Object> itr = jsonArray.iterator();
    	while(itr.hasNext()) {
    		try {
    			JSONObject jsonObj = (JSONObject)itr.next();  
    			listOfMaps.add(new ObjectMapper().readValue(jsonObj.toString(), HashMap.class));
			} catch (JsonMappingException e) {
				log.error("JSON mapping exception: ", e);
			} catch (JsonProcessingException e) {
				log.error("JSON processing exception: ", e);
			}
    	}

    	return listOfMaps;
    }
}
