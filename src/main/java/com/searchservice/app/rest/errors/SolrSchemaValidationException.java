package com.searchservice.app.rest.errors;

public class SolrSchemaValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	public SolrSchemaValidationException(String message) {
		super(message);
	}
}