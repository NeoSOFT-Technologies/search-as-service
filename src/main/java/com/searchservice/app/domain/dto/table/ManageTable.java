package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("tableName")
public class ManageTable {
	
	private String tableName;
	private List<SchemaField> columns;

	public ManageTable(ManageTable schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.columns = schemaDTO.getColumns();
	}
}
