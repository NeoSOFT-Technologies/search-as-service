package com.searchservice.app.domain.dto.table;

import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

//Merged With SchemaFieldDTO 
public class SchemaFieldDTO implements VersionedObjectMapper {

	String name;
	String type;
	String default_;
	boolean isRequired;
	boolean isFilterable;
	boolean isStorable;
	boolean isMultiValue;
	boolean isSortable;
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
