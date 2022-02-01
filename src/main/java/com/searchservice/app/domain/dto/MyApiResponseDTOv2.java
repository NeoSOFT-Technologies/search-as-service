package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.searchservice.app.infrastructure.adaptor.versioning.Versioned;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTOv2 implements Versioned {
	private String responseStatus;
	private String responseMessage1;
	private String responseMessage2;
	@Override
	public Versioned toVersion(int version) {
		System.out.println(">>>>>>>>>> v2 <<<<<<<<< :: "+responseMessage1+": "+responseMessage2);
		if(version <= 1) {
			return new MyApiResponseDTOv1(
					responseStatus, 
					responseMessage1+": "+responseMessage2);
		} else
			return this;
	}
}
