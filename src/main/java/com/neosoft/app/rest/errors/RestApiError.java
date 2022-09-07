package com.neosoft.app.rest.errors;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.neosoft.app.domain.dto.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiError extends BaseResponse {

	private HttpStatus status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	private RestApiError() {
		timestamp = LocalDateTime.now();
	}

	RestApiError(HttpStatus status, String message) {
		this();
		super.statusCode = status.value();
		this.status = status;
		super.message = message;
	}
}