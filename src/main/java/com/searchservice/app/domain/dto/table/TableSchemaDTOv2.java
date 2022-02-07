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
public class TableSchemaDTOv2 implements VersionedObjectMapper {

	private int statusCode;
	private String message;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	
	public TableSchemaDTOv2(TableSchemaDTOv2 schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();	
	}
	
	public TableSchemaDTOv2(String schemaName, List<SchemaFieldDTO> attributes) {
		this.schemaName = schemaName;
		this.attributes = attributes;
	}

	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}