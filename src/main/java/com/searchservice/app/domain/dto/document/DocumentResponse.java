package com.searchservice.app.domain.dto.document;

import com.searchservice.app.domain.dto.table.SchemaField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DocumentResponse {

	private String tableName;
	private String name;
	private SchemaField[] attributes;
	private int statusCode;

	public DocumentResponse(DocumentResponse documentResponseDTO) {
		this.tableName = documentResponseDTO.getTableName();
		this.name = documentResponseDTO.getName();
		this.attributes = documentResponseDTO.getAttributes();
		this.statusCode = documentResponseDTO.getStatusCode();
	}

	public DocumentResponse(String tableName, String name, SchemaField[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}

}