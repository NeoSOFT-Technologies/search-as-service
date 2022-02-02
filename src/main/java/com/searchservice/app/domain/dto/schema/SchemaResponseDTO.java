package com.searchservice.app.domain.dto.schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SchemaResponseDTO {
	//random
	private String tableName;
	private String name;
	private FieldDTO[] attributes;
	private int statusCode;
	private String message;
	
	public SchemaResponseDTO(SchemaResponseDTO schemaResponseDto) {
		this.tableName = schemaResponseDto.getTableName();
		this.name= schemaResponseDto.getName();
		this.attributes= schemaResponseDto.getAttributes();
		this.statusCode= schemaResponseDto.getStatusCode();
		this.message= schemaResponseDto.getMessage();
	}
	
	public SchemaResponseDTO(String tableName, String name, FieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}


}