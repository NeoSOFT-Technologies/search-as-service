package com.searchservice.app.domain.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.SchemaField;
import com.searchservice.app.domain.dto.table.TableInfo;

public class ManageTableUtil {
	
	private ManageTableUtil() {}
	
	public static boolean checkIfListContainsSchemaColumn(List<SchemaField> list, SchemaField schemaColumn) {
		for(SchemaField column: list) {
			if(column.getName().equals(schemaColumn.getName()))
				return true;
		}
		return false;
	}

	public static List<Response.TableListResponse> getTableListForTenant(
			List<String> data, Map<String, String> tableTenantMap, int tenantId){
		List<Response.TableListResponse> tableList = new ArrayList<>();
		
		for (String table : data) {
			String tableName = table.split("_")[0];
			int currentTenantId = Integer.parseInt(table.split("_")[1]);
			if(tenantId == currentTenantId)
				tableList.add(new Response.TableListResponse(
						tableTenantMap.get(tableName), currentTenantId, tableName));
		}
	    	
		return tableList;
	}
	
	public static List<Response.TableListResponse> getPaginatedTableList(
			List<String> data, Map<String, String> tableTenantMap, int pageNumber, int pageSize){
		List<Response.TableListResponse> paginatedTableList = new ArrayList<>();
		
		int currIdx = pageNumber > 1 ? (pageNumber -1) * pageSize : 0;
		for (int i = 0; i < pageSize && i < data.size() - currIdx; i++) {
			String tableName = data.get(i + currIdx).split("_")[0];
			int tenantId = Integer.parseInt(data.get(i + currIdx).split("_")[1]);
			paginatedTableList.add(
					new Response.TableListResponse(
							(tableTenantMap.containsKey(tableName) && tableTenantMap.get(tableName) != null)
							? tableTenantMap.get(tableName) : "No Tenant Found", 
									tenantId, 
									tableName));
		}
	    	
		return paginatedTableList;
	}

	public static List<Response.TableListResponse> getPaginatedTableListForTenant(
			List<String> data, Map<String, String> tableTenantMap, int pageNumber, int pageSize, int tenantId){
		List<Response.TableListResponse> paginatedTableList = getPaginatedTableList(
				data, tableTenantMap, pageNumber, pageSize);
		List<Response.TableListResponse> paginatedTableListForTenant = new ArrayList<>();
		
		for(Response.TableListResponse table: paginatedTableList) {
			// Set table to the result if given tenantId is associated with it
			if(tenantId == table.getTenantId())
				paginatedTableListForTenant.add(table);
		}
	    	
		return paginatedTableListForTenant;
	}

	public static Map<String, SchemaField> removeExistingFields(
													Map<String, SchemaField> newFieldsHashMap, 
													List<SchemaField> newFields, 
													List<Map<String, Object>> schemaFields) {
		List<String> existingAttributesNames = new ArrayList<>();
		boolean newFieldFound = false;
		
		for (int i = 0; i < newFields.size(); i++) {
			SchemaField fieldDto = newFields.get(i);
			boolean isPresent = false;
			for (Map<String, Object> field : schemaFields) {
				if (field.get("name").equals(fieldDto.getName())) {
					isPresent = true;
					existingAttributesNames.add(fieldDto.getName());
					break;
				}
			}
			if (!isPresent)
				newFieldFound = true;
		}
		// If No new schema field is found, RETURN
		if (!newFieldFound) {
			newFieldsHashMap = new HashMap<>();
		} else {
			if (!existingAttributesNames.isEmpty()) {
				// REMOVE existing attributess from newAttributes list
				for (String attributeName : existingAttributesNames) {
					newFieldsHashMap.remove(attributeName);
				}
			}
		}
		return newFieldsHashMap;
	}
	
	public static TableInfo getTableInfoFromClusterStatus(String clusterStatusResponseString, String tableName) {
		JSONObject clusterStatusJson = new JSONObject(clusterStatusResponseString);
		JSONObject clusterObject = clusterStatusJson.getJSONObject("cluster");
		JSONObject collectionsObject = clusterObject.getJSONObject("collections");
		JSONObject myCollectionObject = collectionsObject.getJSONObject(tableName);

		// Extract No. of shards
		JSONObject shardsObject = myCollectionObject.getJSONObject("shards");
		
		int shardsCounter = 1;
		int noOfShards = 0;
		JSONObject shardJsonTemp = shardsObject.getJSONObject("shard"+shardsCounter);
		while(shardJsonTemp != null) {
			noOfShards = shardsCounter;
			try {
				shardJsonTemp = shardsObject.getJSONObject("shard"+(++shardsCounter));
			} catch(JSONException e) {
				break;
			}
		}
		
		TableInfo tableInfo = new TableInfo();
		tableInfo.setReplicationFactor(Integer.parseInt(myCollectionObject.get("replicationFactor").toString()));
		tableInfo.setNoOfShards(noOfShards);
		
		return tableInfo;
	}

	public static Map<String, String> getUserPropsFromJsonResponse(String response) {
		JSONObject configOverlayJson = new JSONObject(response);
		JSONObject overlayObject = configOverlayJson.getJSONObject("overlay");
		JSONObject userPropsObject = overlayObject.getJSONObject("userProps");

		Map<String, String> userPropsMap = new HashMap<>();
		Iterator<String> itr = userPropsObject.keys();
		while(itr.hasNext()) {
			String key = itr.next();
			String value = userPropsObject.get(key).toString();
			userPropsMap.put(key, value);
		}

		return userPropsMap;
	}

}
