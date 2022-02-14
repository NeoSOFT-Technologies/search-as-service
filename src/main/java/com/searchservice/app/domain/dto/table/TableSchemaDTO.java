package com.searchservice.app.domain.dto.table;

import java.util.List;
import java.util.Map;

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
public class TableSchemaDTO implements VersionedObjectMapper {

	@JsonIgnore
	private int statusCode;
	@JsonIgnore
	private String message;
	@JsonIgnore
	private String tableName;
	@JsonIgnore
	private String schemaName;
	private List<SchemaFieldDTO> attributes;
	@JsonIgnore
	private Map<Object, Object> tableDetails;

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
//					statusCode, 
					message, 
					"NoooTHinggggg"
//					attributes, 
					).toVersion(version);
		}
		
		return this;
	}
}
