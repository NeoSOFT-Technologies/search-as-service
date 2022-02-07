package com.searchservice.app.domain.dto.table;

import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSetDTO implements VersionedObjectMapper {
	String baseConfigSetName;
	String configSetName;
	
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
