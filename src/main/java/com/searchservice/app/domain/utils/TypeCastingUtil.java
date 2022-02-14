package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.util.NamedList;

import lombok.Data;

@Data
public class TypeCastingUtil {
	private TypeCastingUtil() {}
	
	
	public static List<String> castToListOfStrings(Object obj) {
		List<String> resultList = new ArrayList<>();
		if (obj instanceof List<?>) {
			for (Object o : (List<?>) obj)
				resultList.add(String.class.cast(o));
		}
		return resultList;
	}
	
	
	public static Map<Object, Object> castFromObjectToMap(Object obj) {
		Map<Object, Object> resultMap = new HashMap<>();
		if (obj instanceof Map) {
			resultMap.put("responseMap", obj);
		}
		return resultMap;
	}
	
	
	public static Map<Object, Object> castFromNamedListOfObjectsToMap(NamedList<Object> obj) {
		Map<Object, Object> resultMap = new HashMap<>();
		if (obj instanceof Map) {
			resultMap.put("responseMap", obj);
		}
		return resultMap;
	}
}
