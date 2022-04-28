package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.domain.dto.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableSchema extends BaseResponse {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class TableSchemaData {
		private String tableName;
		private List<SchemaField> columns;

		public TableSchemaData() {
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

	private TableSchemaData data = new TableSchemaData();

	public TableSchema(ManageTable manageTable) {
		this.data.setTableName(manageTable.getTableName());
		this.data.setColumns(manageTable.getColumns());
	}

}