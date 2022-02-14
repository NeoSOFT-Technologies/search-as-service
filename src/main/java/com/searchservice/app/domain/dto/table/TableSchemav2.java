package com.searchservice.app.domain.dto.table;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableSchemav2 implements VersionedObjectMapper {

	public static class TableSchemav2Data {
        private String tableName;
        private List<SchemaField> columns;
        private Map<Object, Object> tableDetails;

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

        public Map<Object, Object> getTableDetails() {
            return tableDetails;
        }

        public void setTableDetails(Map<Object, Object> tableDetails) {
            this.tableDetails = tableDetails;
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
		this.data.setTableDetails(schemaResponseDTO.getTableDetails());
	}
	
//	public TableSchemaDTOv2(TableSchemaDTO schemaResponseDTO) {
//		this.statusCode=schemaResponseDTO.getStatusCode();
//		this.message=schemaResponseDTO.getMessage();
//		this.schemaName=schemaResponseDTO.getSchemaName();
//		this.attributes=schemaResponseDTO.getAttributes();
//		
//	}
	
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}