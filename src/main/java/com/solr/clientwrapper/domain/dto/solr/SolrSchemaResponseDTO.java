package com.solr.clientwrapper.domain.dto.solr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSchemaResponseDTO {

	private String tableName;
	private String name;
	private SolrFieldDTO[] attributes;
	private int statusCode;
	public SolrSchemaResponseDTO(SolrSchemaResponseDTO solrSchemaResponseDto) {
		this.tableName = solrSchemaResponseDto.getTableName();
		this.name=solrSchemaResponseDto.getName();
		this.attributes=solrSchemaResponseDto.getAttributes();
	}
	
	public SolrSchemaResponseDTO(String tableName, String name, SolrFieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
	
}
