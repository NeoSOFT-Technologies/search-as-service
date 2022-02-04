package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TableSchemaResponseDTO implements VersionedObjectMapper {

	private int statusCode;
	private String message;
	private String tableName;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	
	public TableSchemaResponseDTO(TableSchemaResponseDTO schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.tableName = schemaResponseDTO.getTableName();
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();	
	}
	
	public TableSchemaResponseDTO(String tableName, String schemaName, List<SchemaFieldDTO> attributes) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.attributes = attributes;
	}

	@Override
	public VersionedObjectMapper toVersion(int version) {
		if(version >= 2) {
			return new TableSchemaResponseDTOv2(
					statusCode, 
					message, 
					schemaName, 
					attributes).toVersion(version);
		}
		
		return this;
	}
}