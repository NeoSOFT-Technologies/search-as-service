package com.searchservice.app.domain.dto;


import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTO implements VersionedObjectMapper {
	private String responseStatus;
	private int responseStatusCode;
	private String responseMessage1;
	private String responseMessage2;
	@Override
	public VersionedObjectMapper toVersion(int version) {
		if(version <= 2) {
			return new MyApiResponseDTOv2(
					responseStatus, 
					responseMessage1, 
					responseMessage2).toVersion(version);
		} else
			return this;
	}
}