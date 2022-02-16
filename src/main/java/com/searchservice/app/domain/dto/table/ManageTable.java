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
public class ManageTable implements VersionedObjectMapper {

	private String tableName;
	private String sku;
	@JsonIgnore
	private String schemaName;
	private List<SchemaField> columns;
	@JsonIgnore
	private String tableNewName;
	
	public ManageTable(ManageTable manageTableDTO) {
		this.tableName = manageTableDTO.getTableName();
		this.sku = manageTableDTO.getSku();
		this.schemaName=manageTableDTO.getSchemaName();
		this.columns=manageTableDTO.getColumns();
		this.tableNewName = manageTableDTO.tableNewName;
	}
	
	public ManageTable(String tableName, String sku ,String schemaName, List<SchemaField> attributes2) {
		this.tableName = tableName;
		this.sku = sku;
		this.schemaName = schemaName;
		this.columns = attributes2;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}