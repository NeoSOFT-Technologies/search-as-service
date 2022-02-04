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
public class ManageTableDTO {

	private String tableName;
	private String sku;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	
	public ManageTableDTO(ManageTableDTO manageTableDTO) {
		this.tableName = manageTableDTO.getTableName();
		this.sku = manageTableDTO.getSku();
		this.schemaName=manageTableDTO.getSchemaName();
		this.attributes=manageTableDTO.getAttributes();	
	}
	
	public ManageTableDTO(String tableName, String schemaName, List<SchemaFieldDTO> attributes) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.attributes = attributes;
	}
}