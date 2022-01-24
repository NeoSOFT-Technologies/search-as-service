package com.searchservice.app.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrDocumentResponseDTO {

	
	private String tableName;
	private String name;
	private SolrFieldDTO[] attributes;
	private int statusCode;
	public SolrDocumentResponseDTO(SolrDocumentResponseDTO solrDocumentResponseDTO) {
		this.tableName = solrDocumentResponseDTO.getTableName();
		this.name=solrDocumentResponseDTO.getName();
		this.attributes=solrDocumentResponseDTO.getAttributes();
		this.statusCode=solrDocumentResponseDTO.getStatusCode();
	}
	
	public SolrDocumentResponseDTO(String tableName, String name, SolrFieldDTO[] attributes) {
		this.tableName = tableName;
		this.name = name;
		this.attributes = attributes;
	}
	
}