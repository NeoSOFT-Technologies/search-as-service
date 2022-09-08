package com.neosoft.app.rest.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final int exceptionCode;
	private final HttpStatusCode status;
	private final String exceptionMessage;

}
