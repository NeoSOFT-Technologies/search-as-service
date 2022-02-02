package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ApiResponseDTO implements VersionedObjectMapper {
	private int responseStatusCode;
	private String responseMessage;
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
