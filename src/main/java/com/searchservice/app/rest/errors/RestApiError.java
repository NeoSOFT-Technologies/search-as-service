package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.domain.dto.BaseResponse;

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

	RestApiError(HttpStatus status, Throwable ex) {
		this();
		super.statusCode = status.value();
		this.status = status;
		super.message = ex.getLocalizedMessage();
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}