package com.searchservice.app.domain.utils;

import java.util.List;

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


}
