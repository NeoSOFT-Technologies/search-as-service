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
public class TableSchemaResponse2 {

	private int statusCode;
	private String message;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	
	public TableSchemaResponse2(TableSchemaResponse2 schemaResponseDTO) {
		this.statusCode=schemaResponseDTO.getStatusCode();
		this.message=schemaResponseDTO.getMessage();
		this.schemaName=schemaResponseDTO.getSchemaName();
		this.attributes=schemaResponseDTO.getAttributes();	
	}
	
	public TableSchemaResponse2(String schemaName, List<SchemaFieldDTO> attributes) {
		this.schemaName = schemaName;
		this.attributes = attributes;
	}

}