package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.table.SchemaField;

public class ManageTableUtil {
	private ManageTableUtil() {}
	
	public static boolean checkIfListContainsSchemaColumn(List<SchemaField> list, SchemaField schemaColumn) {
		for(SchemaField column: list) {
			if(column.getName().equals(schemaColumn.getName()))
				return true;
		}
		return false;
	}

	public static List<Response.TableListResponse> getPaginatedTabaleList(List<String> data,int pageNumber, int pageSize){
		List<Response.TableListResponse> paginatedTableList = new ArrayList<>();
		int currIdx = pageNumber > 1 ? (pageNumber -1) * pageSize : 0;
	    for (int i = 0; i < pageSize && i < data.size() - currIdx; i++) {
	    	paginatedTableList.add(new Response.TableListResponse(Integer.parseInt(data.get(i).split("_")[1]),
	    			data.get(i).split("_")[0]));
	    }
		return paginatedTableList;
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


}
