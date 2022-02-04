package com.searchservice.app.domain.dto.document;


import com.searchservice.app.domain.dto.table.SchemaFieldDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DocumentDTO {

	String tableName;
	String name;
	SchemaFieldDTO[] attributes;

	public DocumentDTO(DocumentDTO documentDTO) {
		this.tableName = documentDTO.getTableName();
		this.name = documentDTO.getName();
		this.attributes= documentDTO.getAttributes();
	}
}
