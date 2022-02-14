package com.searchservice.app.domain.utils;

import java.util.HashMap;
import java.util.Map;

public class ManageTableUtil {
	private ManageTableUtil() {}
	
	
	public static Map<Object, Object> getTableInfoFromClusterStatusResponseObject(Object obj, String tableName) {

		Map<Object, Object> resultMap = new HashMap<>();
		if (obj instanceof HashMap<?, ?>) {
			Object cluster = getObjectValueFromObjectKey((Map<?, ?>)obj, "cluster");
			if(cluster instanceof HashMap<?, ?>) {
				Object tables = getObjectValueFromObjectKey((Map<?, ?>)cluster, "collections");
				if(tables instanceof HashMap<?, ?>) {
					resultMap.put(
							"tableDetails", 
							getObjectValueFromObjectKey(
									(Map<?, ?>)tables, tableName));
				}
			}
		}

		return resultMap;
	}
	
	
	public static Object getObjectValueFromObjectKey(Map<?, ?> map, String key) {
		for(Map.Entry<?, ?> entry: ((HashMap<?, ?>)map).entrySet()) {
			if(entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	
	public static Map<Object, Object> getSolrClusterInfo(Object obj) {

		Map<Object, Object> resultMap = new HashMap<>();
		if (obj instanceof HashMap<?, ?>) {
			for(Map.Entry<?, ?> e: ((HashMap<?, ?>)obj).entrySet()) {
				if(e.getKey().equals("cluster"))
					resultMap.put("clusterInfo", e.getValue());
			}
		}
		return resultMap;
	}
}
