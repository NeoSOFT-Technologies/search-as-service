package com.searchservice.app.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSchemaResponseDTO {
	//random
	private String tableName;
	private String name;
	private SolrFieldDTO[] attributes;
	private int statusCode;
	public SolrSchemaResponseDTO(SolrSchemaResponseDTO solrSchemaResponseDto) {
		this.tableName = solrSchemaResponseDto.getTableName();
		this.name=solrSchemaResponseDto.getName();
		this.attributes=solrSchemaResponseDto.getAttributes();
		this.statusCode=solrSchemaResponseDto.getStatusCode();
	}
	
	public SolrSchemaResponseDTO(String tableName, String name, SolrFieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}


}