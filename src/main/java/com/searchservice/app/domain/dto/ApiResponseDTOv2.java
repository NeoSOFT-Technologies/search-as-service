package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ApiResponseDTOv2 {
	private int responseStatusCode;
	private String responseMessage;
	private String responseStatus;
}
