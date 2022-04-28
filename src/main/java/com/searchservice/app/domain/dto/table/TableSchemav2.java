package com.searchservice.app.domain.dto.table;

import java.util.List;
import java.util.Map;

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
public class TableSchemav2 {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TableSchemav2Data {
		private String tableName;
		private List<SchemaField> columns;

		public TableSchemav2Data() {
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public List<SchemaField> getColumns() {
			return columns;
		}

		public void setColumns(List<SchemaField> columns) {
			this.columns = columns;
		}
	}

	private int statusCode;
	private String message;
	private TableSchemav2Data data = new TableSchemav2Data();

	public TableSchemav2(TableSchema schemaResponseDTO) {
//		this.statusCode=schemaResponseDTO.getStatusCode();
//		this.message=schemaResponseDTO.getMessage();
		this.data.setTableName(schemaResponseDTO.getTableName());
		this.data.setColumns(schemaResponseDTO.getColumns());
	}

}