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
        private String schemaName;
        private List<SchemaField> columns;
        private Map<Object, Object> tableDetails;

        public TableSchemav2Data() {
        }

        public String getSchemaName() {
            return schemaName;
        }

        public void setSchemaName(String schemaName) {
            this.schemaName = schemaName;
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
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.data.setSchemaName(schemaResponseDTO.getSchemaName());
		this.data.setColumns(schemaResponseDTO.getAttributes());
		this.data.setTableDetails(schemaResponseDTO.getTableDetails());
	}
	
//	public TableSchemaDTOv2(TableSchemaDTO schemaResponseDTO) {
//		this.statusCode=schemaResponseDTO.getStatusCode();
//		this.message=schemaResponseDTO.getMessage();
//		this.schemaName=schemaResponseDTO.getSchemaName();
//		this.attributes=schemaResponseDTO.getAttributes();
//		
//	}
	
	public TableSchemav2(TableSchemav2 schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.data.setSchemaName(schemaResponseDTO.getData().getSchemaName());
		this.data.setColumns(schemaResponseDTO.getData().getColumns());	
	}
	
	public TableSchemav2(String schemaName, List<SchemaField> attributes) {
		this.data.setSchemaName(schemaName);
		this.data.setColumns(attributes);
	}
	
	public TableSchemav2(String message, String schemaName) {
		this.message = message;
		this.data.setSchemaName(schemaName);
	}

	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}