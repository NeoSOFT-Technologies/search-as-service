package com.searchservice.app.domain.dto.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.domain.dto.table.SchemaField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//DocumentResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document {

	String tableName;
	String name;
	SchemaField[] attributes;
	int statusCode;

	public Document(Document documentDTO) {
		this.tableName = documentDTO.getTableName();
		this.name = documentDTO.getName();
		this.attributes = documentDTO.getAttributes();
		this.statusCode = documentDTO.getStatusCode();
	}

	public Document(String tableName, String name, SchemaField[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}

}
