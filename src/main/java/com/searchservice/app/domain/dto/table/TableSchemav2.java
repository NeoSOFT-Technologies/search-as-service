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

	private int statusCode;
	private String message;
	private String schemaName;
	private List<SchemaField> attributes;
	private Map<Object, Object> tableDetails;
	
	public TableSchemav2(TableSchema schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();
		this.tableDetails=schemaResponseDTO.getTableDetails();
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
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();	
	}
	
	public TableSchemav2(String schemaName, List<SchemaField> attributes) {
		this.schemaName = schemaName;
		this.attributes = attributes;
	}
	
	public TableSchemav2(String message, String schemaName) {
		this.message = message;
		this.schemaName = schemaName;
	}

	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}