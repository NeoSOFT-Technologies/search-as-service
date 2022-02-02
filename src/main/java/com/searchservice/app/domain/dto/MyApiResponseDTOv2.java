package com.searchservice.app.domain.dto;


import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTOv2 implements VersionedObjectMapper {
	private String responseStatus;
	private String responseMessage1;
	private String responseMessage2;
	@Override
	public VersionedObjectMapper toVersion(int version) {
		if(version <= 1) {
			return new MyApiResponseDTOv1(
					responseStatus, 
					responseMessage1+": "+responseMessage2);
		} else
			return this;
	}
}
