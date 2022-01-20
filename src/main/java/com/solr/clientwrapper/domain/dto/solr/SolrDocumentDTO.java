package com.solr.clientwrapper.domain.dto.solr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SolrDocumentDTO {

	String tableName;
	String name;
	SolrFieldDTO[] attributes;

	public SolrDocumentDTO(SolrDocumentDTO solrDocumentDTO) {
		this.tableName = solrDocumentDTO.getTableName();
		this.name = solrDocumentDTO.getName();
		this.attributes=solrDocumentDTO.getAttributes();
	}
}
