package com.searchservice.app.domain.dto;

public class ResponseMessages {
	private ResponseMessages() {}
    public static final String BAD_REQUEST_MSG = "Bad Request call made. Unable to perform the request";
    public static final String DEFAULT_EXCEPTION_MSG = "REST call could not be performed";
    public static final String SUCCESS_RESPONSE_MSG = "REST call processed successfully";
}
