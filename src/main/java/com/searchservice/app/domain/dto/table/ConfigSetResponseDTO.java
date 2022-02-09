package com.searchservice.app.domain.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigSetResponseDTO implements VersionedObjectMapper {

    private int statusCode;
    private String message;
    private List<String> configSets;
	
    public ConfigSetResponseDTO(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
    
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
