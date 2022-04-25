package com.searchservice.app.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response extends BaseResponse{

	private List<String> data;
	private String token;

	public Response(String token) {
		this.token = token;
	}

	public Response(int statusCode, String message) {
		super(statusCode, message);
		
	}

	public Response(int statusCode, String message, String token) {
		super(statusCode, message);
		this.token = token;
	}
	

}
