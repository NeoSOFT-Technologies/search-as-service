package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageTable {

	private String tableName;
	private String sku;
	private List<SchemaField> columns;


	public ManageTable(ManageTable manageTableDTO) {
		this.tableName = manageTableDTO.getTableName();
		this.sku = manageTableDTO.getSku();
		this.columns = manageTableDTO.getColumns();
		
	}

	public ManageTable(String tableName, String sku, String schemaName, List<SchemaField> columns) {
		this.tableName = tableName;
		this.sku = sku;
		this.columns = columns;
	}

}