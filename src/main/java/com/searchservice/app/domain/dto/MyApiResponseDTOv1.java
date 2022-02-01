package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.searchservice.app.infrastructure.adaptor.versioning.Versioned;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponseDTOv1 implements Versioned {
	private String responseStatus;
	private String responseMessage;
	@Override
	public Versioned toVersion(int version) {
		System.out.println(">>>>>>>>>> v1 <<<<<<<<< :: "+responseMessage);
		return this;
	}
}
