package com.searchservice.app.domain.dto.schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

//SchemaResponseDTO

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemaDTO {

	String tableName;
	String name;
	FieldDTO[] attributes;
	int statusCode;

	public SchemaDTO(SchemaDTO schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.name = schemaDTO.getName();
		this.attributes= schemaDTO.getAttributes();
		this.statusCode = schemaDTO.getStatusCode();
	}
	
	public SchemaDTO(String tableName, String name, FieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
}
