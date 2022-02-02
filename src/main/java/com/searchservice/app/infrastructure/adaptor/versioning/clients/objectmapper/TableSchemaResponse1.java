package com.searchservice.app.infrastructure.adaptor.versioning.clients.objectmapper;

import java.util.List;

import com.searchservice.app.domain.dto.SchemaFieldDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TableSchemaResponse1 {

	private int statusCode;
	private String message;
	private String tableName;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	
	public TableSchemaResponse1(TableSchemaResponse1 schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.tableName = schemaResponseDTO.getTableName();
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();	
	}
	
	public TableSchemaResponse1(String tableName, String schemaName, List<SchemaFieldDTO> attributes) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.attributes = attributes;
	}
}