package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.domain.dto.schema.FieldDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//TableSchemaDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableSchemaDTO {

    int statusCode;
	String message;
	String tableName;
	String schemaName;
	List<FieldDTO> attributes;

	public TableSchemaDTO(TableSchemaDTO schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.schemaName = schemaDTO.getSchemaName();
		this.attributes=schemaDTO.getAttributes();
		this.statusCode = schemaDTO.getStatusCode();
		this.message = schemaDTO.getMessage();
	}
	
	public TableSchemaDTO(String tableName, String schemaName, List<FieldDTO> attributes) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.attributes = attributes;
	}
}
