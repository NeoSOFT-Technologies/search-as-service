package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.searchservice.app.infrastructure.adaptor.versioning.Versioned;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTO implements Versioned {
	private String responseStatus;
	private int responseStatusCode;
	private String responseMessage1;
	private String responseMessage2;
	@Override
	public Versioned toVersion(int version) {
		System.out.println(">>>>>>>>>> v3 <<<<<<<<< :: "+responseMessage1+": "+responseMessage2);
		if(version <= 2) {
			return new MyApiResponseDTOv2(
					responseStatus, 
					responseMessage1, 
					responseMessage2).toVersion(version);
		} else
			return this;
	}
}
