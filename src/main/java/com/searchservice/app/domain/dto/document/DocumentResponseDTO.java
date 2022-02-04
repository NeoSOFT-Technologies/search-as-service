package com.searchservice.app.domain.dto.document;


import com.searchservice.app.domain.dto.table.SchemaFieldDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DocumentResponseDTO {

	
	private String tableName;
	private String name;
	private SchemaFieldDTO[] attributes;
	private int statusCode;
	public DocumentResponseDTO(DocumentResponseDTO documentResponseDTO) {
		this.tableName = documentResponseDTO.getTableName();
		this.name= documentResponseDTO.getName();
		this.attributes= documentResponseDTO.getAttributes();
		this.statusCode= documentResponseDTO.getStatusCode();
	}
	
	public DocumentResponseDTO(String tableName, String name, SchemaFieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
	
}