package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.searchservice.app.domain.dto.schema.FieldDTO;

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
	private List<FieldDTO> attributes;
	private String tableNewName;
	
	public ManageTableDTO(ManageTableDTO manageTableDTO) {
		this.tableName = manageTableDTO.getTableName();
		this.sku = manageTableDTO.getSku();
		this.schemaName=manageTableDTO.getSchemaName();
		this.attributes=manageTableDTO.getAttributes();
		this.tableNewName = manageTableDTO.tableNewName;
	}
	
	public ManageTableDTO(String tableName, String sku ,String schemaName, List<FieldDTO> attributes2) {
		this.tableName = tableName;
		this.sku = sku;
		this.schemaName = schemaName;
		this.attributes = attributes2;
	}
}