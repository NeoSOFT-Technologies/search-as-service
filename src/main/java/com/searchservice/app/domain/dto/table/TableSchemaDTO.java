package com.searchservice.app.domain.dto.table;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

//TableSchemaDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableSchemaDTO implements VersionedObjectMapper {

    int statusCode;
	String message;
	@JsonIgnore
	String tableName;
	String schemaName;
	List<SchemaFieldDTO> attributes;

	public TableSchemaDTO(TableSchemaDTO schemaDTO) {
		this.tableName = schemaDTO.getTableName();
		this.schemaName = schemaDTO.getSchemaName();
		this.attributes=schemaDTO.getAttributes();
		this.statusCode = schemaDTO.getStatusCode();
		this.message = schemaDTO.getMessage();
	}
	
	public TableSchemaDTO(String tableName, String schemaName, List<SchemaFieldDTO> attributes) {
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.attributes = attributes;
	}
	
	public TableSchemaDTO(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		if(version >= 2) {
			return new TableSchemaDTOv2(
					statusCode, 
					message, 
					schemaName, 
					attributes).toVersion(version);
		}
		
		return this;
	}
}
