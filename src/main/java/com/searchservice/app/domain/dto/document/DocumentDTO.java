package com.searchservice.app.domain.dto.document;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.domain.dto.table.SchemaFieldDTO;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

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
public class DocumentDTO implements VersionedObjectMapper {

	String tableName;
	String name;
	SchemaFieldDTO[] attributes;
	int statusCode;


	public DocumentDTO(DocumentDTO documentDTO) {
		this.tableName = documentDTO.getTableName();
		this.name = documentDTO.getName();
		this.attributes= documentDTO.getAttributes();
		this.statusCode = documentDTO.getStatusCode();
	}
	
	public DocumentDTO(String tableName, String name, SchemaFieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
