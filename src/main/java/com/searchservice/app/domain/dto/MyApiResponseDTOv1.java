package com.searchservice.app.domain.dto;


import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTOv1 implements VersionedObjectMapper {
	private String responseStatus;
	private String responseMessage;
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
