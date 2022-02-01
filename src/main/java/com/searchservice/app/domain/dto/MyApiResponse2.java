package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyApiResponse2 {
	private String responseStatus;
	private String responseMessage1;
	private String responseMessage2;
}
