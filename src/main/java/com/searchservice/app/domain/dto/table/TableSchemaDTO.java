package com.searchservice.app.domain.dto.table;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TableSchemaDTO {

	String tableName;
	String schemaName;
	List<SchemaFieldDTO> attributes;

	public TableSchemaDTO(TableSchemaDTO schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.schemaName = schemaDTO.getSchemaName();
		this.attributes=schemaDTO.getAttributes();
	}
}
