package com.solr.clientwrapper.domain.dto.solr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class SolrSchemaDTO {

	public SolrSchemaDTO(SolrSchemaDTO solrSchemaDTO) {
		this.tableName = solrSchemaDTO.getTableName();
		this.name = solrSchemaDTO.getName();
		this.attributes=solrSchemaDTO.getAttributes();
		// TODO Auto-generated constructor stub
	}
	private String tableName;
	private String name;
	private SolrFieldDTO[] attributes;

	
}
