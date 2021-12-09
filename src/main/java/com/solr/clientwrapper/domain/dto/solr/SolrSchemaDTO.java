package com.solr.clientwrapper.domain.dto.solr;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrSchemaDTO {

	String tableName;
	String name;
	SolrFieldDTO[] attributes;

	public SolrSchemaDTO(SolrSchemaDTO solrSchemaDTO) {
		this.tableName = solrSchemaDTO.getTableName();
		this.name = solrSchemaDTO.getName();
		this.attributes=solrSchemaDTO.getAttributes();
	}
}
