package com.searchservice.app.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyApiResponse1 {
	private String responseStatus;
	private String responseMessage;
}
