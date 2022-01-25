package com.searchservice.app.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SchemaDTO {

	String tableName;
	String name;
	FieldDTO[] attributes;

	public SchemaDTO(SchemaDTO schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.name = schemaDTO.getName();
		this.attributes= schemaDTO.getAttributes();
	}
}
