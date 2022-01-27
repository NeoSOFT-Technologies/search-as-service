package com.solr.clientwrapper.domain.dto.document;

import com.solr.clientwrapper.domain.dto.schema.FieldDTO;
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
	FieldDTO[] attributes;

	public DocumentDTO(DocumentDTO documentDTO) {
		this.tableName = documentDTO.getTableName();
		this.name = documentDTO.getName();
		this.attributes= documentDTO.getAttributes();
	}
}
