package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ManageTableDTO implements VersionedObjectMapper {

	private String tableName;
	private String sku;
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	@JsonIgnore
	private String tableNewName;
	
	public ManageTableDTO(ManageTableDTO manageTableDTO) {
		this.tableName = manageTableDTO.getTableName();
		this.sku = manageTableDTO.getSku();
		this.schemaName=manageTableDTO.getSchemaName();
		this.attributes=manageTableDTO.getAttributes();
		this.tableNewName = manageTableDTO.tableNewName;
	}
	
	public ManageTableDTO(String tableName, String sku ,String schemaName, List<SchemaFieldDTO> attributes2) {
		this.tableName = tableName;
		this.sku = sku;
		this.schemaName = schemaName;
		this.attributes = attributes2;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}