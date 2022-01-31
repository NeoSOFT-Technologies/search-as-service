package com.searchservice.app.rest.errors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerAdvice {

	@ExceptionHandler(BadRequestOccurredException.class)
	public ResponseEntity<Object> handleBadRequestOccurred(
			BadRequestOccurredException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.BAD_REQUEST, 
										exception.getExceptionMessage()));
	}
	
	@ExceptionHandler(ContentNotFoundException.class)
	public ResponseEntity<Object> handleRestApiDefaultException(
			ContentNotFoundException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.NOT_FOUND, 
										exception.getExceptionMessage()));
	}
	
	@ExceptionHandler(NullPointerOccurredException.class)
	public ResponseEntity<Object> handleNullPointerOccurredException(
			NullPointerOccurredException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.NOT_FOUND, 
										exception.getExceptionMessage()));
	}

	private ResponseEntity<Object> frameRestApiException(RestApiError err) {
		return new ResponseEntity<>(err, err.getStatus());
	}
}
