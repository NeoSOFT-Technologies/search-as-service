package com.searchservice.app.rest.errors;

public class SchemaValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	public SchemaValidationException(String message) {
		super(message);
	}
}
