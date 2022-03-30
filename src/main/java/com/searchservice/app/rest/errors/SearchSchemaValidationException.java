package com.searchservice.app.rest.errors;

public class SearchSchemaValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	public SearchSchemaValidationException(String message) {
		super(message);
	}
}
