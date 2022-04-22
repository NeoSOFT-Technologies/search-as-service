package com.searchservice.app.domain.dto.table;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TableSchema {

	@JsonIgnore
	private String tableName;
	private List<SchemaField> columns;
	@JsonIgnore
	private Map<Object, Object> tableDetails;

	public TableSchema(TableSchema schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.columns = schemaDTO.getColumns();

	}

	public TableSchema(String tableName, List<SchemaField> attributes) {
		this.tableName = tableName;
		this.columns = attributes;
	}

}
