package com.searchservice.app.domain.dto.document;

import com.searchservice.app.domain.dto.schema.FieldDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DocumentResponseDTO {

	
	private String tableName;
	private String name;
	private FieldDTO[] attributes;
	private int statusCode;
	public DocumentResponseDTO(DocumentResponseDTO documentResponseDTO) {
		this.tableName = documentResponseDTO.getTableName();
		this.name= documentResponseDTO.getName();
		this.attributes= documentResponseDTO.getAttributes();
		this.statusCode= documentResponseDTO.getStatusCode();
	}
	
	public DocumentResponseDTO(String tableName, String name, FieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
	
}